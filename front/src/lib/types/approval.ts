export type ApprovalDocumentType =
  | "EXPENSE"
  | "PROCUREMENT"
  | "LEAVE"
  | "OTHER";

export type ApprovalDocumentStatus =
  | "IN_PROGRESS"
  | "APPROVED"
  | "REJECTED"
  | "CANCELLED";

export interface ApprovalDocument {
  id: number;
  drafterId: number;
  documentType: ApprovalDocumentType;
  title: string;
  amount: number | null;
  status: ApprovalDocumentStatus;
  currentStep: number;
  totalSteps: number;
  approverIds: number[];
  draftedAt: string;
  finalizedAt: string | null;
}
