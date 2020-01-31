import React from "react";
import { ChipSet } from "@material/react-chips";
import Tag from "../Tag/Tag";

function TagList(props) {
  if (!props.tags || props.tags.length === 0) {
    return null;
  }

  return (
    <ChipSet>
      {props.tags.map(oneTag => (
        <Tag key={oneTag.id} tag={oneTag} />
      ))}
    </ChipSet>
  );
}

export default TagList;
