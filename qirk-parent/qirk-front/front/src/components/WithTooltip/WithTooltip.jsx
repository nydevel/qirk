import React from "react";
import ReactTooltip from "react-tooltip";
import { renderToStaticMarkup } from "react-dom/server";
import { deepPurple } from "@material-ui/core/colors";

export default function WithTooltip({
  dataTip = "",
  id = -1,
  style = {},
  place = "left",
  type = "dark",
  effect = "solid",
  children = {}
}) {
  return (
    <>
      <ReactTooltip id={id} place={place} type={type} effect={effect} />
      <div
        data-tip={renderToStaticMarkup(<>{dataTip}</>)}
        data-for={id}
        style={{ cursor: "pointer" }}
      >
        <div
          style={{ ...style, borderBottom: `2px dashed ${deepPurple[300]}` }}
        >
          {children}
        </div>
      </div>
    </>
  );
}
