import { useEffect } from "react";

export default (ref: any, onOutsideClick: any, onInsideClick: any) => {
  const handleClickOutside = (event: any) => {
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
