import React, { useState } from "react";
import axios from "../../utils/axios";
import endpoints from "../../utils/endpoints";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import { t } from "i18next";
import Button from "../../components/Button/Button";
import OrganizationListItem from "./OrganizationListItem/OrganizationListItem";
import paths from "../../routes/paths";
import "./MyOrganizations.sass";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import Loading from "../../components/Loading/Loading";
import {
  errorIsNotAuthenticated,
  responseIsStatusOk
} from "../../utils/variousUtils";

function MyOrganizations() {
  const [orgs, setOrgs] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const fetchOrgs = async () => {
    try {
      const response = await axios.get(endpoints.ORGANIZATION_LIST);
      if (responseIsStatusOk(response)) {
        setOrgs(response.data.data);
      }
    } catch (err) {
      if (errorIsNotAuthenticated(err)) {
        // ok, just ignore
      } else {
        toast.error(t("Errors.Error"));
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <PrivatePage className="organization-list" onLoginConfirmed={fetchOrgs}>
      <div>
        <h1>{t("my_organizations_label")}</h1>
        <div className="organization-list__create-button">
          <Link to={paths.OrganizationCreate.toPath()}>
            <Button>{t("Organization create")}</Button>
          </Link>
        </div>
        {isLoading && <Loading />}
        {!isLoading &&
          orgs &&
          orgs.length > 0 &&
          orgs.map(item => <OrganizationListItem item={item} key={item.id} />)}
      </div>
    </PrivatePage>
  );
}

export default MyOrganizations;
