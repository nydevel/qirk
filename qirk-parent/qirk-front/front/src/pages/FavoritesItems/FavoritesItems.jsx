import React from "react";
import PrivatePage from "../../components/PrivatePage/PrivatePage";
import Favorites from "../../components/Dashboard/Favorites/Favorites";
import "./FavoritesItems.sass";
import { useTranslation } from "react-i18next";

function FavoritesItems() {const{t}=useTranslation()
  return (
    <PrivatePage className="favorites-list-items">
      <h1>{t("favorites")}</h1>
      <Favorites page={true} divider={false} heading={false} />
    </PrivatePage>
  );
}

export default FavoritesItems;
