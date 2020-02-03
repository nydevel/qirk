import React from "react";
import "./CreatableSelect.sass";
import CreatableSelect from "react-select/lib/Creatable";

const createOption = label => ({
  label,
  value: label
});

// TODO: не допускать создание дубликатов
class CreatableInputOnly extends React.Component {
  state = {
    inputValue: "",
    value: []
  };

  componentDidMount() {
    this.setState({ value: this.props.value });
  }

  handleChange = value => {
    this.setState({ value });
  };
  handleInputChange = inputValue => {
    this.setState({ inputValue });
  };
  handleKeyDown = event => {
    const { inputValue, value } = this.state;
    if (!inputValue) return;
    switch (event.key) {
      case "Enter":
      case "Tab":
        if (value.find(i => i.value === inputValue)) {
          break;
        }
        this.setState(
          {
            inputValue: "",
            value: [...value, createOption(inputValue)]
          },
          () => this.props.onChange(this.state.value)
        );
        event.preventDefault();
        break;
      default: //nothing
    }
  };
  render() {
    const { inputValue, value } = this.state;
    return (
      <React.Fragment>
        <CreatableSelect
          className={`react-creatable-select ${this.props.className}`}
          components={{
            DropdownIndicator: null
          }}
          inputValue={inputValue}
          isClearable
          isMulti
          onChange={this.handleChange}
          ref={ref => (this.input = ref)}
          menuIsOpen={false}
          onInputChange={this.handleInputChange}
          onKeyDown={this.handleKeyDown}
          placeholder={this.props.placeholder}
          value={value}
        />
        <input
          tabIndex={-1}
          value={value}
          required={this.props.required}
          onChange={e => this.props.onChange(e && e.value)}
          style={{
            opacity: 0,
            width: 0,
            height: 0,
            left: "50%",
            position: "absolute"
          }}
          onFocus={() => this.input.focus()}
        />
      </React.Fragment>
    );
  }
}

export default CreatableInputOnly;
