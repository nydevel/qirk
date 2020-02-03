import React from "react";
import { Chip } from "@material/react-chips";

function Tag({ tag: { name, id } }) {
  return <Chip id={id} label={name} />;
}

export default Tag;
