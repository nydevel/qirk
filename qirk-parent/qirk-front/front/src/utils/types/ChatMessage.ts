import { ChatSender } from "./ChatSender";

export type ChatMessage = {
  sender_id: ChatSender["id"];
  timestamp: { iso8601: string; epoch_milli: number };
  message: string;
  external_uuid: string;
};
