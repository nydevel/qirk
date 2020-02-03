export const favIdOrderListRfomFavList = favList => {
  const order = [];
  favList.forEach(fav => {
    const id = fav.id;
    order.push(id);
  });
  return order;
};
