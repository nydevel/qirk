// https://eslint.org/docs/user-guide/configuring

module.exports = {
  root: true,
  parser: "babel-eslint",
  parserOptions: {
    sourceType: "module",
    ecmaFeatures: {
      jsx: true,
    }
  },
  env: {
    "browser": true,
    "node": true,
    "es6": true
  },
  extends: [
    "eslint:recommended",
    "plugin:react/recommended"
  ],
  plugins: [
    "react",
    "class-property"
  ],
  // check if imports actually resolve
  settings: {
    'import/resolver': {
      webpack: {
        config: 'build/webpack.base.conf.js'
      }
    },
    react: {
      version: 'detect'
    }
  },
  // add your custom rules here
  rules: {
    'react/display-name': 'off',
    'no-case-declarations': 'off',
    'no-console': 'off',
    'react/prop-types': 0,
    'no-param-reassign': ['error', {
      props: true,
      ignorePropertyModificationsFor: [
        'state', // for vuex state
        'acc', // for reduce accumulators
        'e' // for e.returnvalue
      ]
    }],
    // allow debugger during development
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    "no-return-assign": 0,
    "camelcase": 0,
    "prefer-destructuring": 0,
    "no-mixed-operators": 0,
    "no-unused-expressions": 0,
    "no-continue": 0,
    "no-restricted-syntax": 0,
    "guard-for-in": 0,
    "import/extensions": 0,
    "no-new": 0,
    "no-undef": 1,
    "import/prefer-default-export": 0,
    "no-restricted-globals": 0,
    "no-shadow": 0,
    "no-tabs": 0,
    "no-mixed-spaces-and-tabs": 0,
    "consistent-return": 0,
    "import/no-extraneous-dependencies": 0
  }
};
