import React, { useState, useRef, useEffect } from "react";
import { v4 } from "uuid";
import classNames from "classnames";
import { ChipSet, Chip } from "@material/react-chips";
import MaterialIcon from "@material/react-material-icon";
import useOutsideClickListener from "../../utils/hooks/useOutsideClickListener";
import "./CreatableChips.sass";

const enterIshKeys = ["Enter"];
const enterIshSymbols = [";", ","];

function CreatableChips({
  onChange,
  maxLength,
  value,
  label,
  id,
  unique,
  style
}) {
  const chipsFromValue = value => value.map(v => ({ id: v4(), value: v }));

  const wrapperRef = useRef(null);
  const inputRef = useRef(null);
  const [text, setText] = useState("");
  const [chips, setChips] = useState(chipsFromValue(value));
  const [wrapperOutlined, setWrapperOutlined] = useState(false);

  useEffect(() => {
    if (value) {
      if (chips.length !== value.length) {
        setChips(chipsFromValue(value));
      }
    }
  }, [value]);

  const add = () => {
    const textValue = text.trim();

    if (textValue && (!unique || !chips.some(ch => ch.value === textValue))) {
      addChip(textValue);
    }

    setText("");
  };

  const focusInput = () => {
    inputRef.current.focus();
  };

  useOutsideClickListener(
    wrapperRef,
    () => {
      add();
      setWrapperOutlined(false);
    },
    () => {
      focusInput();
      setWrapperOutlined(true);
    }
  );

  const removeChip = id => {
    setChips(chips.filter(ch => ch.id !== id));
  };

  const addChip = textValue => {
    setChips([...chips, { value: textValue, id: v4() }]);
  };

  const notify = () => {
    if (onChange) {
      onChange(chips.map(ch => ch.value));
    }
  };

  useEffect(() => {
    notify();
  }, [chips]);

  const onInputChange = e => {
    const newText = e.target.value;
    if (enterIshSymbols.some(s => newText.includes(s))) {
      enterIshSymbols.forEach(s => {
        text.replace(s, "");
      });
      add();
    } else {
      setText(newText);
    }
  };

  const onKeyPress = e => {
    if (enterIshKeys.some(k => k === e.key)) {
      e.preventDefault();
      add();
    }
  };

  return (
    <div
      ref={wrapperRef}
      onClick={() => focusInput()}
      className={classNames({
        "creatable-chips-wrapper": true,
        outlined: wrapperOutlined
      })}
      style={style}
    >
      <div className="creatableChips">
        <ChipSet>
          {chips.map(ch => (
            <Chip
              key={ch.id}
              id={ch.id}
              label={ch.value}
              trailingIcon={
                <MaterialIcon onClick={() => removeChip(ch.id)} icon="cancel" />
              }
            />
          ))}
          <input
            style={{
              background: "transparent",
              border: 0,
              outline: "none",
              marginLeft: 8,
              height: 40
            }}
            ref={inputRef}
            id={id}
            value={text}
            onKeyPress={onKeyPress}
            onChange={onInputChange}
            maxLength={maxLength}
          />
        </ChipSet>
      </div>
      <div
        className={classNames({
          label: true,
          collapsed: wrapperOutlined || chips.length > 0 || text
        })}
      >
        <span>
          <label onClick={() => focusInput()} htmlFor={id}>
            {label}
          </label>
        </span>
      </div>
    </div>
  );
}

export default CreatableChips;
