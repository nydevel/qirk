import React, { useState, useEffect } from "react";

const ExpandingListScrollNearBottomListener = ({
  triggerBelow = 350,
  amountOfItems = 1,
  onScrolledNearBottom = () => console.error("check onScrolledNearBottom prop"),
  render = p => <div {...p} />,
  ...props
}) => {
  const [itemsReached, setItemsReached] = useState(null);

  useEffect(() => {
    if (itemsReached === amountOfItems) {
      onScrolledNearBottom();
    }
  }, [amountOfItems, itemsReached, onScrolledNearBottom]);

  const handleScroll = e => {
    const pxAboveBottom =
      e.target.scrollHeight - e.target.scrollTop - e.target.clientHeight;

    if (pxAboveBottom < triggerBelow) {
      setItemsReached(amountOfItems);
    }
  };

  return render({ onScroll: handleScroll, ...props });
};

export default ExpandingListScrollNearBottomListener;
