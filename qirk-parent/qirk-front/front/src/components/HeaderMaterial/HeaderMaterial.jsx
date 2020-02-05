import React, { useState, useEffect, useRef } from "react";
import { withRouter, Link } from "react-router-dom";
import { t } from "i18next";
import { connect } from "react-redux";
import queryString from "query-string";
import classNames from "classnames";
import {
  TopAppBarIcon,
  TopAppBarRow,
  TopAppBarSection,
  TopAppBarTitle
} from "@material/react-top-app-bar";
import MaterialIcon from "@material/react-material-icon";
import Button from "@material/react-button";
import constants from "../../utils/constants";
import ic_arrow_down_v from "./../../assets/icons/ic_arrow_down_v.svg";
import defaultAvatar from "./../../assets/pictures/defaultAvatar.jpg";
import paths from "../../routes/paths";
import { uiUserFromUser } from "../../utils/variousUtils";
import { Animated } from "react-animated-css";
import Card, { CardActionButtons } from "@material/react-card";
import NotficationIcon from "../NotficationIcon/NotficationIcon";
import { logoutUser } from "./../../actions/authActions";
import { changeDashboardSizeDispatch } from "./../../actions/uiActions";
import "./HeaderMaterial.sass";
import Balance from "../Balance/Balance";

function HeaderMaterial({
  isSignedIn,
  location,
  logoutUser,
  changeDashboardSizeDispatch,
  user,
  history
}) {
  const [searchType, setSearchType] = useState(constants.SEARCH_EVERYWHERE);
  const [searchString, setSearchString] = useState("");
  const [showSearchOptions, setShowSearchOptions] = useState(false);
  const [searchInFocus, setSearchInFocus] = useState(true);
  const searchRef = useRef(null);
  const profileMenuRef = useRef(null);
  const profileMenuIconRef = useRef(null);

  useEffect(() => {
    if (searchInFocus && searchString) {
      setShowSearchOptions(true);
    } else {
      setShowSearchOptions(false);
    }
  }, [searchInFocus, searchString]);

  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleClickOutside = e => {
    if (
      profileMenuRef &&
      profileMenuRef.current && !profileMenuRef.current.contains(e.target) &&
      profileMenuIconRef.current &&
        !profileMenuIconRef.current.contains(e.target)
    ) {
      setIsProfileMenuOpen(false);
    }
    if (
      searchRef &&
      searchRef.current &&
      searchRef.current.contains(e.target)
    ) {
      setSearchInFocus(true);
    } else {
      setSearchInFocus(false);
    }
  };

  const logout = async () => {
    await logoutUser();
    setIsProfileMenuOpen(false);
    history.push(paths.Login.toPath());
  };

  const submit = e => {
    e.preventDefault();
    setSearchInFocus(false);
    switch (searchType) {
      case constants.SEARCH_EVERYWHERE:
        history.push(paths.Search.toPath({ query: searchString }));
        break;
      case constants.SEARCH_USERS:
        history.push(
          paths.Search.toPath({ query: searchString, tab: "users" })
        );
        break;
      case constants.SEARCH_PROJECTS:
        history.push(
          paths.Search.toPath({ query: searchString, tab: "projects" })
        );
        break;
      case constants.SEARCH_ORGANIZATIONS:
        history.push(
          paths.Search.toPath({ query: searchString, tab: "organizations" })
        );
        break;
      default:
        throw new Error("Unknown type of search");
    }
    setSearchString("");
  };
  return (
    <TopAppBarRow className="header qa_header">
      <TopAppBarSection className="left" align="start">
        {isSignedIn && (
          <TopAppBarIcon navIcon tabIndex={0}>
            <MaterialIcon
              hasRipple
              icon="menu"
              onClick={changeDashboardSizeDispatch}
            />
          </TopAppBarIcon>
        )}

        <Link style={{ color: "white" }} to="/">
          <TopAppBarTitle
            style={{
              paddingLeft: 5,
              fontFamily: "bebas-neue, sans-serif",
              fontWeight: "400",
              fontStyle: "normal",
              textTransform: "uppercase",
              fontSize: "35px",
              display: "flex"
            }}
          >
            <span style={{ display: "flex" }}>
              <span style={{ margin: "auto" }}>Qirk</span>
            </span>
            <span
              style={{
                fontSize: "12px",
                paddingTop: 8,
                paddingLeft: 3,
                height: "29px"
              }}
            >
              beta
            </span>
          </TopAppBarTitle>
        </Link>

        {1 === 2 && (
          <Link to={paths.UserSearch.toPath()}>
            <Button className="white-ripples">{t("Users")}</Button>
          </Link>
        )}
      </TopAppBarSection>
      <TopAppBarSection align="center" role="toolbar">
        {/* && (
          <form ref={searchRef} onSubmit={submit}>
            <fieldset disabled={2 == 1}>
              <div className="main">
                <input
                  required
                  placeholder={t("search_hint")}
                  value={searchString}
                  onChange={e => {
                    setSearchString(e.target.value);
                    setSearchInFocus(true);
                  }}
                />
                <button
                  type="submit"
                  onClick={() => {
                    setSearchType(constants.SEARCH_EVERYWHERE);
                  }}
                >
                  <img src={ic_search} alt={t("search_everywhere_btn")} />
                </button>
              </div>
              {showSearchOptions && (
                <div className="options">
                  <button
                    type="submit"
                    onClick={() => {
                      setSearchType(constants.SEARCH_EVERYWHERE);
                    }}
                  >
                    {t("search_everywhere_btn")}
                  </button>
                  <button
                    type="submit"
                    onClick={() => {
                      setSearchType(constants.SEARCH_ORGANIZATIONS);
                    }}
                  >
                    {t("search_organizations_btn")}
                  </button>
                  <button
                    type="submit"
                    onClick={() => {
                      setSearchType(constants.SEARCH_PROJECTS);
                    }}
                  >
                    {t("search_projects_btn")}
                  </button>
                  <button
                    type="submit"
                    onClick={() => {
                      setSearchType(constants.SEARCH_USERS);
                    }}
                  >
                    {t("search_users_btn")}
                  </button>
                </div>
              )}
            </fieldset>
          </form>
                  ) */}
      </TopAppBarSection>
      <TopAppBarSection align="end" className="end" role="toolbar">
        {isSignedIn ? (
          <>
            {/*<Balance />*/}
            <NotficationIcon />
            <div ref={profileMenuIconRef}>
              <div
                onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)}
                aria-label={t("Profile menu")}
                className="header__new-profile"
              >
                <div className="avatar">
                  <img src={defaultAvatar} alt="" />
                </div>
                <div
                  className={classNames({
                    arrow: true,
                    opened: isProfileMenuOpen
                  })}
                >
                  <img src={ic_arrow_down_v} alt="" />
                </div>
              </div>
            </div>
            <div
              className={classNames({
                "header__profile-menu": true,
                opened: isProfileMenuOpen
              })}
              ref={profileMenuRef}
            >
              <Animated
                animationIn="slideInDown"
                animationOut="slideOutUp"
                animationInDuration={200}
                animationOutDuration={200}
                isVisible={isProfileMenuOpen}
              >
                <Card>
                  <div className="card-header">
                    <div className="user">{uiUserFromUser(user)}</div>
                    <div className="username">
                      {user.email ||
                        user.username /* TODO Denis remove "|| user.username" later */}
                    </div>
                  </div>
                  <CardActionButtons style={{ display: "flex" }}>
                    <Link
                      style={{
                        flex: 1,
                        display: "flex",
                        margin: 0,
                        padding: 0,
                        marginRight: 4
                      }}
                      to={paths.MyProfile.toPath()}
                      onClick={() => setIsProfileMenuOpen(false)}
                    >
                      <Button style={{ flex: 1 }}>{t("Profile")}</Button>
                    </Link>
                    <Button
                      style={{ flex: 1, marginLeft: 4 }}
                      onClick={() => logout()}
                    >
                      {t("Button.Logout")}
                    </Button>
                  </CardActionButtons>
                </Card>
              </Animated>
            </div>
          </>
        ) : (
          <>
            <Link
              className={classNames({
                header__item: true,
                qa_header_sign_in_link: true
              })}
              to={`${paths.Login.toPath()}?${queryString.stringify({
                bounce_to: location.pathname
              })}`}
            >
              <Button
                className={classNames({
                  "white-ripples": true,
                  qa_header_sign_in_btn: true
                })}
                style={{ marginRight: 8 }}
              >
                {t("Sign in")}
              </Button>
            </Link>

            <Link
              className={classNames({
                header__item: true,
                qa_header_sign_up_link: true
              })}
              to={paths.Join.toPath()}
            >
              <Button
                className={classNames({
                  "white-ripples": true,
                  qa_header_sign_up_btn: true
                })}
              >
                {t("Sign up")}
              </Button>
            </Link>
          </>
        )}
      </TopAppBarSection>
    </TopAppBarRow>
  );
}

export default withRouter(
  connect(state => ({ isSignedIn: state.auth.isSignedIn, user: state.user }), {
    logoutUser,
    changeDashboardSizeDispatch
  })(HeaderMaterial)
);
