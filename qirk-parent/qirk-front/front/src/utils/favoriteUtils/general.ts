import { Favorite } from "../types/Favorite";

export const favIdOrderListRfomFavList = (favList: Favorite[]) =>
  favList.map(f => f.id);
