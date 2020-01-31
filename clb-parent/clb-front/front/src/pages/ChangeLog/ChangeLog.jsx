import React, { useEffect, useState } from "react";
import { t } from "i18next";
import axiosStatic from "../../utils/axiosStatic";
import Page from "../../components/Page/Page";
import PublicFooter from "../../components/PublicFooter/PublicFooter";
import Loading from "../../components/Loading/Loading";
import endpoints from "../../utils/endpoints";

function ChangeLog() {
  const [loading, setLoading] = useState(false);
  const [doc, setDoc] = useState("");

  const getChangeLog = async () => {
    try {
      setLoading(true);

      const response = await axiosStatic.get(
        endpoints.GET_STATIC_DATA_CHANGELOG,
        {
          params: {
            time: Date.now()
          }
        }
      );

      if (response.status === 200 && response.data) {
        setDoc(response.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getChangeLog();
  }, []);

  return (
    <Page>
      <h1>{t("changelog")}</h1>
      {loading && <Loading />}
      {!loading && <div dangerouslySetInnerHTML={{ __html: doc }}></div>}
      <PublicFooter />
    </Page>
  );
}

export default ChangeLog;
