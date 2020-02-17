import { NotificationType } from "../enums/NotificationType";

export type NotificationResponse = {
  timestamp: { iso8601: string; epoch_milli: number };
  iso8601: string;
  epoch_milli: number;
  notification_type: NotificationType;
  json: string;
};

export type NotificationLocal = {
    //todo
};
