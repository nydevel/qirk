import React, { useState, useEffect, useRef } from "react";
import { connect } from "react-redux";
import { toast } from "react-toastify";
import { t } from "i18next";
import { withRouter } from "react-router-dom";
import Checkbox from "../../components/Checkbox/Checkbox";
import Button from "../../components/Button/Button";
import Select from "../../components/Select/Select";
import PasswordChangeForm from "./PasswordChange/PasswordChangeForm";
import WithFetch from "../../components/WithFetch/WithFetch";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import Loading from "../../components/Loading/Loading";
import constants from "../../utils/constants";
import { langToOption, responseIsStatusOk } from "../../utils/variousUtils";
import endpoints from "../../utils/endpoints";
import axios from "../../utils/axios";
import useMountEffect from "../../utils/hooks/useMountEffect";
import { scrollToRef } from "../../utils/variousUtils";
import TextInput from "../../components/TextInput/TextInput";
import CreatableChips from "../../components/CreatableChips/CreatableChips";
import "./MyProfile.sass";
import { setUserDispatch } from "../../actions/userActions";

const mapStateToProps = state => ({
  user: state.user,
  langOptions: state.common.languages,
  loadingLanguages: state.common.fetchLanguagesStatus === constants.WAITING
});

function Profile({
  langOptions,
  loadingLanguages,
  setUserDispatch,
  user,
  location: { hash }
}) {
  const [fullName, setFullName] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isWaiting, setIsWaiting] = useState(false);
  const [tags, setTags] = useState([]);
  const [languages, setLanguages] = useState([]);
  const [about, setAbout] = useState("");
  const [dontRecommend, setDontRecommend] = useState(false);

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
      setTags(response.data.data[0].tags.map(e => e.name));
      setLanguages(
        response.data.data[0].languages.map(lang => langToOption(lang))
      );
      setFullName(response.data.data[0].full_name);
      setDontRecommend(response.data.data[0].dont_recommend);

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
        languages: languages.map(option => parseInt(option.value)),
        tags,
        full_name: fullName.trim() || "",
        dont_recommend: false, //dontRecommend
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

              <CreatableChips
                label={t("Technology tags")}
                value={tags}
                maxLength="127"
                onChange={e => setTags(e)}
              />

              {/* <div style={{ paddingTop: 20 }}>
                <Checkbox
                  id="is_public"
                  label={t("Public profile")}
                  checked={!dontRecommend}
                  disabled={isWaiting}
                  onChange={e => setDontRecommend(!e.target.checked)}
                />
              </div> */}
              <div style={{ paddingTop: 30 }}>
                <Select
                  label={t("Languages.self")}
                  isMulti
                  className="qa_profile_form_languages_input"
                  isWaiting={loadingLanguages}
                  options={[...langOptions]}
                  onChange={e => {
                    setLanguages(e);

                    // kostyl to fix lang select clickable area shifting downward

                    const el = document.querySelector(".drawer-app-content");
                    if (el) {
                      el.scrollBy(0, -1);
                      el.scrollBy(0, 1);
                    }
                  }}
                  value={languages}
                />
              </div>

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
  connect(
    mapStateToProps,
    { setUserDispatch }
  )(Profile)
);
