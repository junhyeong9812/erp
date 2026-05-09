import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { ApprovalsView } from "@/features/approvals/approvals-view";
import { ApprovalDraftButton } from "@/features/approvals/approval-draft-button";
import { ApprovalDraftSheet } from "@/features/approvals/approval-draft-sheet";
import { APPROVALS } from "@/lib/mock";

export default async function ApprovalsPage() {
  const tPage = await getTranslations("Pages.approvals");
  const tApprovals = await getTranslations("Approvals");
  const inProgress = APPROVALS.filter((a) => a.status === "IN_PROGRESS").length;
  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tApprovals("subtitleCount", {
          count: APPROVALS.length,
          inProgress,
        })}
        actions={<ApprovalDraftButton />}
      />
      <ApprovalsView />
      <ApprovalDraftSheet />
    </div>
  );
}
