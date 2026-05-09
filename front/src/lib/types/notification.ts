export type NotificationChannel = "EMAIL" | "SMS" | "PUSH" | "SYSTEM";
export type NotificationStatus = "PENDING" | "SENT" | "FAILED";

export interface Notification {
  id: number;
  recipientId: number;
  title: string;
  body: string;
  channel: NotificationChannel;
  status: NotificationStatus;
  sentAt: string | null;
  failureReason: string | null;
}
