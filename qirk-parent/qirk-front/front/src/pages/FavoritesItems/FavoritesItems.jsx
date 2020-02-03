import React from "react";
import { t } from "i18next";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import Favorites from "../../components/Dashboard/Favorites/Favorites";
import "./FavoritesItems.sass";

function FavoritesItems() {
  return (
    <PrivatePage className="favorites-list-items">
      <h1>{t("favorites")}</h1>
      <Favorites page={true} divider={false} heading={false} />
    </PrivatePage>
  );
}

export default FavoritesItems;
