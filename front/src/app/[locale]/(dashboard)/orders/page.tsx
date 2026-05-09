import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { OrdersTable } from "@/features/orders/orders-table";
import { OrderCreateButton } from "@/features/orders/order-create-button";
import { OrderCreateSheet } from "@/features/orders/order-create-sheet";
import { initialOrders } from "@/lib/mock";

export default async function OrdersPage() {
  const tPage = await getTranslations("Pages.orders");
  const tOrders = await getTranslations("Orders");

  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tOrders("subtitleCount", { count: initialOrders.length })}
        actions={<OrderCreateButton />}
      />
      <OrdersTable />
      <OrderCreateSheet />
    </div>
  );
}
