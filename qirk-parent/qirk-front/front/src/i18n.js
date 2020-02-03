import i18next from "i18next";
import detector from "i18next-browser-languagedetector";
import en from "./translations/en";

const resources = {
  en: {
    translation: en
  }
};

i18next.use(detector).init({
  interpolation: {
    escapeValue: false
  },
  resources,
  fallbackLng: "en"
});

export default i18next;
