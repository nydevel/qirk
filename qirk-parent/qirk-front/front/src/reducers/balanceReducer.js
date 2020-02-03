import actions from "../utils/constants";

const INITIAL_STATE = {
  balance: null,
  isLoaded: false
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case actions.DROP_STATE_ON_UNAUTH:
      return { ...INITIAL_STATE };

    case actions.BALANCE_SET_BALANCE_LOADED:
      return { ...state, isLoaded: action.payload };

    case actions.BALANCE_SET_BALANCE:
      return { ...state, balance: action.payload };

    case actions.BALANCE_ADD_TO_BALANCE:
      return { ...state, balance: state.balance + action.payload };

    default:
      return state;
  }
};
