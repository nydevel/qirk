import React, { useState } from "react";
import { t } from "i18next";
import { connect } from "react-redux";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import { toast } from "react-toastify";
import { addToBalanceDispatch } from "../../actions/balanceActions";
import TextInput from "../../components/TextInput/TextInput";
import Button1 from "../../components/Button/Button";
import { uiMoney } from "../../utils/variousUtils";

const MIN_ADD_FUNDS_AMOUNT = 0.01;

const AddBalance = ({ userId, email, addToBalanceDispatch }) => {
  const [amount, setAmount] = useState(50);

  const pay = function() {
    const amountInt = Number(("" + amount).replace(",", "."));
    const uiAmount = uiMoney(amountInt);

    if (amountInt < MIN_ADD_FUNDS_AMOUNT) {
      setAmount(MIN_ADD_FUNDS_AMOUNT);
      return alert(`Minimal amount is $${uiMoney(MIN_ADD_FUNDS_AMOUNT)}`);
    }

    const exchangeRate = process.env.RUB_PER_LOCAL_USD || 60;

    var widget = new cp.CloudPayments({ language: "en-US" });

    widget.charge(
      {
        publicId: process.env.CLOUD_PAYMENTS_PUBLIC_ID,
        description: `$${uiAmount} deposit`,
        amount: amountInt * exchangeRate,
        currency: "RUB",
        email,
        accountId: `${userId}`,
        skin: "classic"
      },
      function(options) {
        const amountInUsd = options.amount / exchangeRate;
        addToBalanceDispatch(amountInUsd);
        toast.success(t(`$${uiMoney(amountInUsd)} Added to your balance`));
      },
      function(reason, options) {
        toast.error(reason);
      }
    );
  };

  return null;

  return (
    <PrivatePage>
      <h1>{t("add_funds")}</h1>
      <form
        onSubmit={e => {
          e.preventDefault();
          pay();
        }}
      >
        <fieldset>
          <TextInput
            onChange={e => setAmount(e.target.value)}
            value={amount}
            label="Amount, $"
            textFieldStyle={{ width: 150 }}
          />
          <Button1>{t("add")}</Button1>
        </fieldset>
      </form>
    </PrivatePage>
  );
};

export default connect(
  state => ({
    userId: state.user && state.user.id,
    email: state.user && state.user.email
  }),
  { addToBalanceDispatch }
)(AddBalance);
