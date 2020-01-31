import React from "react";
import "./LoadingScreen.sass";
import Loading from "../Loading/Loading";

function LoadingScreen({ message }) {
  return (
    <div className="loading-screen">
      <div className="loading-screen__wrapper">
        {/* <div className="loading-screen__speeding-wheel" /> */}
        <Loading size={50} />
        {message && <div className="loading-screen__message">{message}</div>}
      </div>
    </div>
  );
}

export default LoadingScreen;
