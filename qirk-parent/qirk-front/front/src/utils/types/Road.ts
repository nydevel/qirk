import { Card } from "./Card";

export type Road = {
  id: number; // todo

  // local mutations
  cardsOrder: Card["id"][];
};
