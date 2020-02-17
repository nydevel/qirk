import React, { useState, useEffect, useRef } from "react";
import { connect } from "react-redux";
import { toast } from "react-toastify";
import { withRouter } from "react-router-dom";
import Checkbox from "../../components/Checkbox/Checkbox";
import Button from "../../components/Button/Button";
import Select from "../../components/Select/Select";
import PasswordChangeForm from "./PasswordChange/PasswordChangeForm";
import WithFetch from "../../components/WithFetch/WithFetch";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import Loading from "../../components/Loading/Loading";
import constants from "../../utils/constants";
import { responseIsStatusOk } from "../../utils/variousUtils";
import endpoints from "../../utils/endpoints";
import axios from "../../utils/axios";
import useMountEffect from "../../utils/hooks/useMountEffect";
import { scrollToRef } from "../../utils/variousUtils";
import TextInput from "../../components/TextInput/TextInput";
import CreatableChips from "../../components/CreatableChips/CreatableChips";
import "./MyProfile.sass";
import { setUserDispatch } from "../../actions/userActions";
import { useTranslation } from "react-i18next";

const mapStateToProps = state => ({
  user: state.user,
});

function Profile({
  langOptions,
  loadingLanguages,
  setUserDispatch,
  user,
  location: { hash }
}) {const{t}=useTranslation()
  const [fullName, setFullName] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isWaiting, setIsWaiting] = useState(false);
  const [about, setAbout] = useState("");

  const [taskCommentedNotification, setTaskCommentedNotification] = useState(
    true
  );
  const [taskCreatedNotification, setTaskCreatedNotification] = useState(true);
  const [taskUpdatedNotification, setTaskUpdatedNotification] = useState(true);

  const editProfileRef = useRef(null);
  const performScroll = () => scrollToRef(editProfileRef);
  useMountEffect(performScroll);

  useEffect(() => {
    if (hash === constants.HASH_EDIT_PROFILE) {
      performScroll();
    }
  }, [hash]);

  const fetchProfile = async () => {
    try {
      setIsLoading(true);

      const response = await axios.get(endpoints.USER);
      setAbout(response.data.data[0].about);

      setFullName(response.data.data[0].full_name);

      setTaskCommentedNotification(
        response.data.data[0].notification_settings.task_commented
      );
      setTaskCreatedNotification(
        response.data.data[0].notification_settings.task_created
      );
      setTaskUpdatedNotification(
        response.data.data[0].notification_settings.task_updated
      );
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  const updateProfile = async e => {
    e.preventDefault();

    try {
      setIsWaiting(true);

      const data = {
        about: about.trim(),

        full_name: fullName.trim() || "",

        notification_settings: {
          task_commented: taskCommentedNotification,
          task_created: taskCreatedNotification,
          task_updated: taskUpdatedNotification
        }
      };
      const response = await axios.put(endpoints.USER, data);
      if (responseIsStatusOk(response)) {
        toast.success(t("Success"));
        setUserDispatch({
          ...user,
          full_name: fullName || ""
        });
      }
    } catch (e) {
      console.error(e);
      toast.error(t("Errors.Error"));
    } finally {
      setIsWaiting(false);
    }
  };
  return (
    <PrivatePage onLoginConfirmed={fetchProfile} className="profile">
      <WithFetch fetchList={[constants.LAZY_FETCH_LANGUAGES]}>
        <PasswordChangeForm isWaiting={isWaiting} />
        <h1 ref={editProfileRef}>{t("Profile")}</h1>
        {isLoading && <Loading />}
        {!isLoading && (
          <form
            onSubmit={updateProfile}
            className="profile__column"
            disabled={isWaiting}
          >
            <fieldset disabled={isWaiting}>
              <TextInput
                id="full_name"
                maxLength="255"
                required
                value={fullName}
                onChange={e => setFullName(e.target.value)}
                type="text"
                label={t("full_name")}
              />

              <TextInput
                id="about"
                maxLength={10000}
                textFieldStyle={{ width: "100%" }}
                textarea
                value={about}
                onChange={e => setAbout(e.target.value)}
                label={t("about_label")}
              />

              <div style={{ paddingTop: 30 }}>{t("email_notifications")}</div>

              <div style={{ paddingTop: 20 }}>
                <Checkbox
                  id="task_created"
                  label={t("task_created")}
                  checked={taskCreatedNotification}
                  disabled={isWaiting}
                  onChange={e => setTaskCreatedNotification(e.target.checked)}
                />
              </div>

              <div>
                <Checkbox
                  id="task_updated"
                  label={t("task_updated")}
                  checked={taskUpdatedNotification}
                  disabled={isWaiting}
                  onChange={e => setTaskUpdatedNotification(e.target.checked)}
                />
              </div>

              <div>
                <Checkbox
                  id="task_commented"
                  label={t("task_commented")}
                  checked={taskCommentedNotification}
                  disabled={isWaiting}
                  onChange={e => setTaskCommentedNotification(e.target.checked)}
                />
              </div>

              <Button
                className="profile__update-button"
                type="submit"
                isLoading={isWaiting}
              >
                {t("Button.Update")}
              </Button>
            </fieldset>
          </form>
        )}
      </WithFetch>
    </PrivatePage>
  );
}

export default withRouter(
  connect(mapStateToProps, { setUserDispatch })(Profile)
);
