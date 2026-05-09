import { getTranslations } from "next-intl/server";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/icons";
import { PageHead } from "@/components/erp/page-head";
import { OrdersTable } from "@/features/orders/orders-table";
import { initialOrders } from "@/lib/mock";

export default async function OrdersPage() {
  const tPage = await getTranslations("Pages.orders");
  const tOrders = await getTranslations("Orders");

  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tOrders("subtitleCount", { count: initialOrders.length })}
        actions={
          <Button size="sm">
            <Icon.Plus className="size-3.5" />
            {tOrders("newOrder")}
          </Button>
        }
      />
      <OrdersTable />
    </div>
  );
}
