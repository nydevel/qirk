import React, { useEffect } from "react";
import { connect } from "react-redux";
import { withRouter, Link } from "react-router-dom";
import { uiMoney } from "../../utils/variousUtils";
import { fetchTokenAndBalance } from "../../actions/balanceActions";
import paths from "../../routes/paths";

const Balance = props => {
  const {
    loggedId,
    balance,
    balanceLoaded,
    location: { pathname },
    fetchTokenAndBalance
  } = props;

  useEffect(() => {
    if (loggedId) {
      fetchTokenAndBalance();
    }
  }, [pathname, loggedId, fetchTokenAndBalance]);

  if (!balanceLoaded) {
    return null;
  }

  return (
    <div className="balance" style={{ paddingRight: 8 }}>
      {/* <Link
        style={{ textDecoration: "none", cursor: "pointer", color: "white" }}
        to={paths.AddBalance.toPath()}
      > */}
      ${uiMoney(balance)}
      {/* </Link> */}
    </div>
  );
};

export default withRouter(
  connect(
    state => ({
      loggedId: state.auth.isSignedIn,
      balance: state.balance.balance,
      balanceLoaded: state.balance.isLoaded
    }),
    { fetchTokenAndBalance }
  )(Balance)
);
