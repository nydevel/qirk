import { useEffect } from "react";

export default (ref, onOutsideClick, onInsideClick) => {
  const handleClickOutside = event => {
    if (ref.current && !ref.current.contains(event.target)) {
      if (onOutsideClick) {
        onOutsideClick();
      }
    } else {
      if (onInsideClick) {
        onInsideClick();
      }
    }
  };

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  });
};
