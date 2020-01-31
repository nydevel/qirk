import axios from "../utils/axios";
import moneyAxios from "../utils/moneyAxios";
import constants from "../utils/constants";
import { responseIsStatusOk } from "../utils/variousUtils";
import endpoints from "../utils/endpoints";

export function addToBalanceDispatch(sum) {
  function addToBalance(sum) {
    return { type: constants.BALANCE_ADD_TO_BALANCE, payload: sum };
  }

  return function(dispatch) {
    dispatch(addToBalance(sum));
  };
}

export function fetchTokenAndBalance() {
  return async dispatch => {
    try {
      const response = await axios.get(endpoints.GET_USER_CREDIT_TOKEN);
      if (
        responseIsStatusOk(response) &&
        response.data &&
        response.data.data &&
        response.data.data.length > 0 &&
        response.data.data[0]
      ) {
        const iv = response.data.data[0].IV;
        const token = response.data.data[0].token;

        const balanceResponse = await moneyAxios.get(
          endpoints.GET_USER_BALANCE,
          { params: { token, IV: iv } }
        );

        if (
          responseIsStatusOk(balanceResponse) &&
          balanceResponse.data &&
          balanceResponse.data.data &&
          balanceResponse.data.data.length > 0 &&
          balanceResponse.data.data[0]
        ) {
          dispatch({
            type: constants.BALANCE_SET_BALANCE,
            payload: balanceResponse.data.data[0].balance / 100
          });

          dispatch({
            type: constants.BALANCE_SET_BALANCE_LOADED,
            payload: true
          });
        }
      }
    } catch (e) {
      console.error(e);
    }
  };
}
