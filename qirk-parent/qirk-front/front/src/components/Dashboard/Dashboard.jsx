import React, { useEffect } from "react";
import { connect } from "react-redux";
import { withRouter, Link } from "react-router-dom";
import { t } from "i18next";
import {
  fetchFavorites,
  addProjectToFavorites,
  removeFavorite
} from "../../actions/commonActions";
import { setTaskSearchFilterSearchAfterDispatch } from "../../actions/taskSearchActions";
import Favorites from "./Favorites/Favorites";
import toast from "../../utils/hooks/useRequestResultToast";
import paths from "../../routes/paths";
import "./Dashboard.sass";
import PathAuthConfirmed from "../PathAuthConfirmed/PathAuthConfirmed";
import { Cell, Grid, Row } from "@material/react-layout-grid";
import Drawer, {
  DrawerContent,
  DrawerAppContent
} from "@material/react-drawer";
import { TopAppBarFixedAdjust } from "@material/react-top-app-bar";
import List, {
  ListItem,
  ListGroup,
  ListItemText,
  ListDivider
} from "@material/react-list";
import Icon from "@material-ui/core/Icon";
import ExpandingListScrollNearBottomListener from "../ExpandingListScrollNearBottomListener/ExpandingListScrollNearBottomListener";
import constants from "../../utils/constants";
import { unixMillisecondTimestamp } from "../../utils/timeUtils";
import { saveItemToLocalStorageSafe } from "../../utils/variousUtils";
import queryString from "query-string";

function Dashboard({
  requestStatus,
  children,
  isSignedIn,
  favoritesList,
  dashboardCollapsed,
  fetchFavorites,
  lastConfirmedSignedInPathname,
  setTaskSearchFilterSearchAfterDispatch,
  tasks,
  countOfFoundedTasks,
  lastLoginCheckPathname,
  loadingMissingTasks,
  location: { pathname }
}) {
  toast(requestStatus);

  const renderDashboard = () => {
    if (dashboardCollapsed) {
      return (
        <Drawer style={{ width: 75 }}>
          <DrawerContent tag="main">
            <List>
              <ListGroup>
             
                <Link to={paths.MyProjects.toPath()}>
                  <ListItem
                    className="dashboard-list__item__icon-button"
                    style={{
                      margin: 0,
                      display: "flex",
                      flexDirection: "column",
                      minHeight: 55
                    }}
                  >
                    <Icon
                      style={{ color: "rgba(0, 0, 0, 0.54)", paddingTop: 3 }}
                    >
                      work
                    </Icon>
                    <div
                      style={{
                        fontSize: 10,
                        whiteSpace: "normal",
                        textAlign: "center",
                        wordBreak: "break-word",
                        minWidth: 65,
                        paddingTop: 6
                      }}
                    >
                      {t("projects_label")}
                    </div>
                  </ListItem>
                </Link>
              </ListGroup>
              <ListGroup>
                <Link to={paths.FavoritesItems.toPath()}>
                  <ListItem
                    className="dashboard-list__item__icon-button"
                    style={{
                      margin: 0,
                      display: "flex",
                      flexDirection: "column",
                      minHeight: 55
                    }}
                  >
                    <Icon
                      style={{ color: "rgba(0, 0, 0, 0.54)", paddingTop: 3 }}
                    >
                      favorite
                    </Icon>
                    <div
                      style={{
                        fontSize: 10,
                        whiteSpace: "normal",
                        textAlign: "center",
                        wordBreak: "break-word",
                        minWidth: 65,
                        paddingTop: 6
                      }}
                    >
                      {t("favorites")}
                    </div>
                  </ListItem>
                </Link>
              </ListGroup>
              
            </List>
          </DrawerContent>
        </Drawer>
      );
    } else {
      return (
        <Drawer>
          <DrawerContent tag="main">
            <List>
              <ListGroup>
                
                <Link to={paths.MyProjects.toPath()}>
                  <ListItem>
                    <ListItemText primaryText={t("my_projects_label")} />
                  </ListItem>
                </Link>
              </ListGroup>
              <Favorites favorites={favoritesList} />
              
            </List>
          </DrawerContent>
        </Drawer>
      );
    }
  };

  useEffect(() => {
    renderDashboard();
    saveItemToLocalStorageSafe("dashboard_collapsed", dashboardCollapsed);
  }, [dashboardCollapsed]);

  useEffect(() => {
    if (isSignedIn && lastConfirmedSignedInPathname === pathname) {
      fetchFavorites();
    }
  }, [isSignedIn, pathname, lastConfirmedSignedInPathname]);

  const isLandingPage = !isSignedIn && pathname === paths.Home.url;
  const isProjectViewPage = isSignedIn && pathname === lastLoginCheckPathname;

  return (
    <TopAppBarFixedAdjust className="page-under-app-bar-flex-element">
      <PathAuthConfirmed>{renderDashboard()}</PathAuthConfirmed>
      <ExpandingListScrollNearBottomListener
        amountOfItems={tasks && tasks.length}
        onScrolledNearBottom={() => {
          if (
            !loadingMissingTasks &&
            isProjectViewPage &&
            tasks &&
            tasks.length &&
            countOfFoundedTasks &&
            tasks.length < countOfFoundedTasks
          ) {
            const { searchCol } = queryString.parse(window.location.search);
            searchCol === constants.TASK_SORT_BY.UPDATED_AT
              ? setTaskSearchFilterSearchAfterDispatch(
                  unixMillisecondTimestamp(tasks[tasks.length - 1].updated_at)
                )
              : setTaskSearchFilterSearchAfterDispatch(
                  unixMillisecondTimestamp(tasks[tasks.length - 1].created_at)
                );
          }
        }}
        render={p => <DrawerAppContent className="drawer-app-content" {...p} />}
      >
        {!isLandingPage && (
          <Grid>
            <Row>
              <Cell columns={1} />
              <Cell columns={10}>{children}</Cell>
              <Cell columns={1} />
            </Row>
          </Grid>
        )}
        {isLandingPage && <>{children}</>}
      </ExpandingListScrollNearBottomListener>
    </TopAppBarFixedAdjust>
  );
}

const mapStateToProps = state => ({
  favoritesList: state.favorites.listOfFavorites,
  requestStatus: state.common.requestStatus,
  isSignedIn: state.auth.isSignedIn,
  lastConfirmedSignedInPathname: state.auth.lastConfirmedSignedInPathname,
  dashboardCollapsed: state.ui.dashboardCollapsed,
  tasks: state.taskSearch.result,
  countOfFoundedTasks: state.taskSearch.countOfAllTasks,
  lastLoginCheckPathname: state.auth.lastLoginCheckPathname,
  loadingMissingTasks: state.taskSearch.loadingMissingTasks
});

export default withRouter(
  connect(mapStateToProps, {
    fetchFavorites,
    addProjectToFavorites,
    removeFavorite,
    setTaskSearchFilterSearchAfterDispatch
  })(Dashboard)
);
