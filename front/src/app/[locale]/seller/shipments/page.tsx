import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { SellerShipmentsView } from "@/features/seller/seller-shipments-view";
import {
  DEFAULT_SELLER_ID,
  initialOrders,
  initialShipments,
} from "@/lib/mock";
import { sellerShipments } from "@/features/seller/helpers";

export default async function SellerShipmentsPage() {
  const t = await getTranslations("Seller.shipments");
  const count = sellerShipments(
    DEFAULT_SELLER_ID,
    [...initialShipments],
    [...initialOrders]
  ).length;

  return (
    <div className="px-6 py-6">
      <PageHead title={t("title")} sub={t("subtitle", { count })} />
      <SellerShipmentsView sellerId={DEFAULT_SELLER_ID} />
    </div>
  );
}
