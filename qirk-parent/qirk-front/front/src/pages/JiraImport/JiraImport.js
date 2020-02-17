import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { connect } from "react-redux";

import {
  Button,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Paper
} from "@material-ui/core";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import constants from "../../utils/constants";
import WithFetch from "../../components/WithFetch/WithFetch";
import axios from "../../utils/axios";
import endpoints from "../../utils/endpoints";
import {
  responseIsStatusOk,
  errorIsEntitiesFileNotFound
} from "../../utils/variousUtils";
import Loading from "../../components/Loading/Loading";
import UploadListItem from "./UploadListItem";
import paths from "../../routes/paths";
import { useTranslation } from "react-i18next";

const JiraImport = ({
  orgId,
  orgUiId,
  history,
  loadingOrg,
  match: {
    params: { organization_uiid }
  }
}) => {
  const { t } = useTranslation();
  const [loading, setLoading] = useState(false);
  const [uploads, setUploads] = useState([]);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    const fetchUploads = orgId => {
      setLoading(true);
      axios
        .get(endpoints.GET_JIRA_IMPORT_LIST_UPLOADS, {
          params: { organization_id: orgId }
        })
        .then(response => {
          if (responseIsStatusOk(response)) {
            setUploads(
              response.data.data.sort(
                (a, b) => b.timestamp.epoch_milli - a.timestamp.epoch_milli
              )
            );
          }
        })
        .catch(e => console.error(e))
        .finally(() => setLoading(false));
    };

    if (organization_uiid === orgUiId && orgId) {
      fetchUploads(orgId);
    }
  }, [organization_uiid, orgUiId, orgId]);

  useEffect(() => {
    console.log("uploads", uploads);
  }, [uploads]);

  const loaderNeeded = loading || loadingOrg;

  return (
    <PrivatePage>
      <WithFetch fetchList={[constants.FETCH_ORGANIZATION]}>
        <h1>Import tasks from Jira</h1>
        {loaderNeeded && <Loading />}
        {!loaderNeeded && (
          <>
            <input
              accept=".zip"
              id="jira-zip-input"
              style={{ display: "none" }}
              type="file"
              onChange={e => {
                setUploading(true);

                const data = new FormData();

                data.append("file", e.target.files[0]);
                data.append("organization_id", orgId);

                axios
                  .post(endpoints.POST_JIRA_IMPORT_UPLOAD, data, {})
                  .then(res => {
                    const upload =
                      res && res.data && res.data.data && res.data.data[0];

                    if (upload) {
                      setUploads(oldUploads => [upload, ...oldUploads]);

                      const timestamp = upload.timestamp.epoch_milli;

                      history.push(
                        paths.JiraImportMapping.toPath({
                          organization_uiid,
                          timestamp
                        })
                      );
                    }

                    toast.success("Uploaded");
                    setUploading(false);
                  })
                  .catch(e => {
                    console.error(e);

                    if (errorIsEntitiesFileNotFound(e)) {
                      toast.error("entities.xml not found");
                    } else {
                      toast.error(t("Errors.Error"));
                    }
                    setUploading(false);
                  });
              }}
            />

            <label htmlFor="jira-zip-input">
              <Button
                disabled={uploading}
                variant="contained"
                style={{ background: "#6200ee", color: "white" }}
                component="span"
              >
                Upload zip
              </Button>
            </label>

            {uploads && uploads.length > 0 && (
              <Paper style={{ marginTop: 16 }}>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>{"Date & Time"}</TableCell>
                      <TableCell>Archive name</TableCell>
                      <TableCell>Imported projects</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {uploads.map(item => (
                      <UploadListItem
                        item={item}
                        organizationUiId={organization_uiid}
                        key={`import_${item.timestamp.epoch_milli}`}
                      />
                    ))}
                  </TableBody>
                </Table>
              </Paper>
            )}
          </>
        )}
      </WithFetch>
    </PrivatePage>
  );
};

export default connect(state => ({}))(JiraImport);
