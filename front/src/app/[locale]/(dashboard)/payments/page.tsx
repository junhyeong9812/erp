import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { PaymentsView } from "@/features/payments/payments-view";
import { PAYMENTS, REFUNDS } from "@/lib/mock";

export default async function PaymentsPage() {
  const tPage = await getTranslations("Pages.payments");
  const tPayments = await getTranslations("Payments");
  const pending = PAYMENTS.filter((p) => p.status === "PENDING").length;
  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tPayments("subtitleCount", {
          count: PAYMENTS.length,
          refunds: REFUNDS.length,
          pending,
        })}
      />
      <PaymentsView />
    </div>
  );
}
