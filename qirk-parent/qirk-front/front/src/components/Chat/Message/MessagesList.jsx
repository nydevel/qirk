import React, { useRef, useEffect } from "react";
import { connect } from "react-redux";
import Message from "./Message";

function MessagesList(props) {
  const refMessages = useRef([]);

  const PREPENDED = "PREPENDED";
  const APPENDED = "APPENDED";

  const REMOVED_LAST = "REMOVED_LAST";
  const REMOVED_FIRST = "REMOVED_FIRST";

  const DATASET_CHANGED = "DATASET_CHANGED";

  const SCROLL_TOLERANCE_PX = 25;

  const figureOutTypeOfChange = (prevArr, currentArr) => {
    if (prevArr.length === 0) {
      return APPENDED;
    }

    const firstMessagesStayedTheSame =
      prevArr[0].message === currentArr[0].message &&
      prevArr[0].sender_id === currentArr[0].sender_id;

    const lastMessagesStayedTheSame =
      prevArr[prevArr.length - 1].message ===
        currentArr[currentArr.length - 1].message &&
      prevArr[prevArr.length - 1].sender_id ===
        currentArr[currentArr.length - 1].sender_id;

    const messagesAdded = prevArr.length < currentArr.length;
    const messagesRemoved = prevArr.length > currentArr.length;

    if (
      messagesAdded &&
      firstMessagesStayedTheSame &&
      !lastMessagesStayedTheSame
    ) {
      return APPENDED;
    }

    if (
      messagesAdded &&
      !firstMessagesStayedTheSame &&
      lastMessagesStayedTheSame
    ) {
      return PREPENDED;
    }

    if (
      messagesRemoved &&
      !firstMessagesStayedTheSame &&
      lastMessagesStayedTheSame
    ) {
      return REMOVED_FIRST;
    }

    if (
      messagesRemoved &&
      firstMessagesStayedTheSame &&
      !lastMessagesStayedTheSame
    ) {
      return REMOVED_LAST;
    }

    return DATASET_CHANGED;
  };

  const figureOutDiffLength = (prevArr, currentArr) =>
    currentArr.length - prevArr.length;

  const getMessageHeight = pos => {
    // pos - starts from 1
    const element = document.querySelector(
      `.messages-list .message-single-wrapper:nth-child(${pos})`
    );
    return element.scrollHeight;
  };

  useEffect(() => {
    if (refMessages.current.length !== props.messages.length) {
      const diffSize = figureOutDiffLength(refMessages.current, props.messages);

      const changeType = figureOutTypeOfChange(
        refMessages.current,
        props.messages
      );

      const containerElement = document.querySelector(".messages-list");

      let newMessagesHeight = 0;

      if (diffSize > 0) {
        for (let i = 0; i < diffSize; i++) {
          if (changeType === APPENDED) {
            newMessagesHeight += getMessageHeight(props.messages.length - i);
          } else if (changeType === PREPENDED) {
            newMessagesHeight += getMessageHeight(i + 1);
          }
        }
      }

      const atBottom =
        containerElement.scrollTop +
          containerElement.offsetHeight +
          newMessagesHeight +
          SCROLL_TOLERANCE_PX >=
        containerElement.scrollHeight;

      switch (changeType) {
        case APPENDED:
          if (atBottom) {
            containerElement.scrollTo(null, containerElement.scrollHeight);
          }
          break;
        case PREPENDED:
          containerElement.scrollTo(
            null,
            containerElement.scrollTop + newMessagesHeight
          );
          break;
        case REMOVED_FIRST:
          break;
        case REMOVED_LAST:
          break;
        case DATASET_CHANGED:
          break;
        default:
          break;
      }

      refMessages.current = [...props.messages];
    }
  }, [props.messages]);

  if (!props.messages || props.messages.length === 0) {
    return null;
  }

  return (
    <div className="messages-list-wrapper">
      <div className="messages-list">
        {props.messages.map((message, index) => (
          <Message key={index} index={index} message={message} />
        ))}
      </div>
    </div>
  );
}

const mapStateToProps = state => ({
  messages: state.chat.messages
});
export default connect(mapStateToProps)(MessagesList);
