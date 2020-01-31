import React, { useEffect, useState, useRef } from "react";
import { connect } from "react-redux";
import { t } from "i18next";
import { Animated } from "react-animated-css";
import { IconButton, Icon, Paper } from "@material-ui/core";
import {
  setNotificationsNeedToSendCheckAllRequestToSocketDispatch,
  setNotificationsLoadAfterDispatch
} from "./../../actions/notificationActions";
import { clickIsInside } from "../../utils/variousUtils";
import NotificationItem from "../NotificationItem/NotificationItem";
import ExpandingListScrollNearBottomListener from "../ExpandingListScrollNearBottomListener/ExpandingListScrollNearBottomListener";

const NotficationIcon = ({
  notifications,
  checkedAtTimestamp,
  isSignedIn,
  setNotificationsNeedToSendCheckAllRequestToSocketDispatch,
  setNotificationsLoadAfterDispatch
}) => {
  const wrapperRef = useRef();
  const popupRef = useRef();

  const [count, setCount] = useState(0);
  const [isOpen, setIsOpen] = useState(false);
  const [unreadNotifications, setUnreadNotifications] = useState([]);
  const [readNotifications, setReadNotifications] = useState([]);

  const handleClickOutside = e => {
    if (clickIsInside(e, wrapperRef) || clickIsInside(e, popupRef)) {
      //setIsOpen(true);
    } else {
      setIsOpen(false);
    }
  };

  useEffect(() => {
    isOpen
      ? setNotificationsNeedToSendCheckAllRequestToSocketDispatch(false)
      : unreadNotifications &&
        unreadNotifications.length > 0 &&
        notifications &&
        notifications.length > 0 &&
        setNotificationsNeedToSendCheckAllRequestToSocketDispatch(true);
  }, [isOpen]);

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  useEffect(() => {
    if (notifications && notifications.length > 0 && checkedAtTimestamp) {
      const unread = [];
      const read = [];
      notifications.filter(n =>
        n.epoch_milli > checkedAtTimestamp ? unread.push(n) : read.push(n)
      );
      setCount(unread.length);
      setUnreadNotifications(unread);
      setReadNotifications(read);
    } else {
      setCount(0);
    }
  }, [notifications, checkedAtTimestamp]);

  if (!isSignedIn) {
    return null;
  }

  return (
    <>
      <div
        ref={wrapperRef}
        onClick={() => {
          setIsOpen(!isOpen);
          setCount(0);
        }}
        style={{ marginRight: 16, position: "relative" }}
      >
        <IconButton>
          <Icon style={{ color: "white" }}>notifications</Icon>
        </IconButton>
        {count > 0 && (
          <div
            style={{
              display: "flex",
              background: "#ec0000",
              color: "white",
              position: "absolute",
              top: 6,
              right: 6,
              width: 15,
              height: 15,
              borderRadius: "1000px"
            }}
          >
            <span
              style={{
                display: "flex",
                margin: "auto",
                fontSize: "10px",
                cursor: "pointer"
              }}
            >
              {count > 9 ? "9+" : count}
            </span>
          </div>
        )}
      </div>
      <div
        style={{
          position: "absolute",
          minWidth: 200,
          top: 72,
          right: 17,
          transition: "all 200ms ease",
          opacity: isOpen ? 1 : 0,
          height: 0,
          overflow: "visible"
        }}
        ref={popupRef}
      >
        <Animated
          animationIn="slideInDown"
          animationOut="slideOutUp"
          animationInDuration={200}
          animationOutDuration={200}
          isVisible={isOpen}
        >
          <ExpandingListScrollNearBottomListener
            amountOfItems={notifications && notifications.length}
            onScrolledNearBottom={() => {
              if (notifications && notifications.length) {
                setNotificationsLoadAfterDispatch(
                  notifications[notifications.length - 1].epoch_milli
                );
              }
            }}
            render={p => <Paper {...p} />}
            style={{ maxWidth: 350, maxHeight: 500, overflow: "auto" }}
          >
            {notifications && notifications.length > 0 && (
              <>
                <div
                  style={{
                    color: "black",
                    padding: 8,
                    borderBottom: "1px solid rgba(0,0,0,.2)",
                    fontWeight: "bold",
                    position: "sticky",
                    top: 0,
                    background: "white"
                  }}
                >
                  {t("notifications")}
                </div>
                <div className="notification-card__items">
                  {unreadNotifications && unreadNotifications.length > 0 && (
                    <>
                      <div
                        style={{
                          color: "rgba(0, 0, 0, 0.5)",
                          padding: 8,
                          borderBottom: "1px solid rgba(0, 0, 0, 0.1)",
                          textTransform: "uppercase",
                          background: "rgba(0, 0, 0, 0.1)",
                          fontSize: "small",
                          fontWeight: "bold"
                        }}
                      >
                        {t("new")}
                      </div>
                      {unreadNotifications.map(n => (
                        <NotificationItem
                          handlerClick={() => setIsOpen(false)}
                          notification={n}
                          key={n.epoch_milli}
                        />
                      ))}
                    </>
                  )}
                  {readNotifications && readNotifications.length > 0 && (
                    <>
                      <div
                        style={{
                          color: "rgba(0, 0, 0, 0.5)",
                          padding: 8,
                          borderBottom: "1px solid rgba(0, 0, 0, 0.1)",
                          textTransform: "uppercase",
                          background: "rgba(0, 0, 0, 0.1)",
                          fontSize: "small",
                          fontWeight: "bold"
                        }}
                      >
                        {t("earlier")}
                      </div>
                      {readNotifications.map(n => (
                        <NotificationItem
                          handlerClick={() => setIsOpen(false)}
                          notification={n}
                          key={n.epoch_milli}
                        />
                      ))}
                    </>
                  )}
                </div>
              </>
            )}

            {(!notifications || notifications.length === 0) && (
              <div
                style={{
                  color: "rgb(49, 49, 49)",
                  padding: 32,
                  fontSize: "15px"
                }}
              >
                {t("no_notifications_yet")}
              </div>
            )}
          </ExpandingListScrollNearBottomListener>
        </Animated>
      </div>
    </>
  );
};

export default connect(
  state => ({
    notifications: state.notifications.notifications,
    checkedAtTimestamp: state.notifications.checkedAtTimestamp,
    isSignedIn: state.auth.isSignedIn
  }),
  {
    setNotificationsNeedToSendCheckAllRequestToSocketDispatch,
    setNotificationsLoadAfterDispatch
  }
)(NotficationIcon);
