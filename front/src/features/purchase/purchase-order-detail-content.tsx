"use client";

import { useTranslations } from "next-intl";
import { useShallow } from "zustand/react/shallow";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { StatusBadge } from "@/components/erp/status-badge";
import {
  SheetClose,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { fmtDate, fmtKRW, fmtN } from "@/lib/format";
import { lookupProduct, lookupWarehouse } from "@/lib/mock";
import { useErpStore } from "@/store/erp-store";

interface Props {
  poId: number;
}

const RECEIVING_WAREHOUSE_ID = 1;

export function PurchaseOrderDetailContent({ poId }: Props) {
  const t = useTranslations("Purchase.panel");
  const tCommon = useTranslations("Common");
  const tToast = useTranslations("Purchase.toast");

  const { po, stock, receive } = useErpStore(
    useShallow((s) => {
      const p = s.purchaseOrders.find((x) => x.id === poId);
      return {
        po: p,
        stock: p
          ? s.stocks.find(
              (st) =>
                st.productId === p.productId &&
                st.warehouseId === RECEIVING_WAREHOUSE_ID
            ) ?? null
          : null,
        receive: s.receivePurchaseOrder,
      };
    })
  );

  if (!po) return null;

  const product = lookupProduct(po.productId);
  const warehouse = lookupWarehouse(RECEIVING_WAREHOUSE_ID);
  const totalAmount = po.quantity * po.unitPrice;
  const received = po.received ?? 0;
  const remaining = po.quantity - received;
  const canReceive = po.status === "ISSUED" || po.status === "PARTIAL";

  const handleReceive = () => {
    const result = receive(po.id);
    if (result.ok) {
      toast.success(tToast("received"));
    }
  };

  return (
    <>
      <SheetHeader>
        <SheetTitle>{t("title", { id: po.id })}</SheetTitle>
        <SheetDescription>
          {po.supplier} · {product.sku}
        </SheetDescription>
      </SheetHeader>

      <div className="flex-1 overflow-y-auto px-5 py-4">
        <div className="mb-5 flex items-center justify-between">
          <span className="font-mono text-[12px] text-text-3">
            PO-{po.id}
          </span>
          <StatusBadge status={po.status} />
        </div>

        <section className="mb-5">
          <h3 className="mb-2 text-[11.5px] font-semibold uppercase tracking-wider text-text-3">
            {t("summary")}
          </h3>
          <dl className="grid grid-cols-[130px_1fr] gap-y-1.5 text-[13px]">
            <dt className="text-text-3">{t("kvSupplier")}</dt>
            <dd className="text-text">{po.supplier}</dd>
            <dt className="text-text-3">{t("kvProduct")}</dt>
            <dd className="text-text">{product.name}</dd>
            <dt className="text-text-3">{t("kvSku")}</dt>
            <dd className="font-mono text-[12px] text-text-2">
              {product.sku}
            </dd>
            <dt className="text-text-3">{t("kvQuantity")}</dt>
            <dd className="tabular-nums text-text">{fmtN(po.quantity)}</dd>
            <dt className="text-text-3">{t("kvReceived")}</dt>
            <dd className="tabular-nums text-text">{fmtN(received)}</dd>
            <dt className="text-text-3">{t("kvRemaining")}</dt>
            <dd
              className={
                remaining > 0
                  ? "tabular-nums font-medium text-warn"
                  : "tabular-nums text-text-3"
              }
            >
              {fmtN(remaining)}
            </dd>
            <dt className="text-text-3">{t("kvUnitPrice")}</dt>
            <dd className="tabular-nums text-text-2">
              {fmtKRW(po.unitPrice)}
            </dd>
            <dt className="text-text-3">{t("kvTotal")}</dt>
            <dd className="font-medium tabular-nums text-text">
              {fmtKRW(totalAmount)}
            </dd>
            <dt className="text-text-3">{t("kvIssuedAt")}</dt>
            <dd className="tabular-nums text-text-2">
              {fmtDate(po.issuedAt)}
            </dd>
          </dl>
        </section>

        <section>
          <h3 className="mb-2 text-[11.5px] font-semibold uppercase tracking-wider text-text-3">
            {t("stockEffect")}
          </h3>
          <div className="rounded-md border border-border bg-panel-2 p-3">
            <div className="flex items-center justify-between text-[13px]">
              <span className="text-text-2">{t("kvWarehouse")}</span>
              <span className="text-text">{warehouse.name}</span>
            </div>
            {stock ? (
              <div className="mt-2 grid grid-cols-3 gap-2 text-[12px]">
                <div className="rounded bg-panel p-2 text-center">
                  <div className="text-[10.5px] text-text-3">현재 총 재고</div>
                  <div className="mt-0.5 font-medium tabular-nums text-text">
                    {fmtN(stock.total)}
                  </div>
                </div>
                <div className="rounded bg-panel p-2 text-center">
                  <div className="text-[10.5px] text-text-3">예약</div>
                  <div className="mt-0.5 font-medium tabular-nums text-text-2">
                    {fmtN(stock.reserved)}
                  </div>
                </div>
                <div className="rounded bg-accent-soft p-2 text-center">
                  <div className="text-[10.5px] text-accent-ink">입고 후 +</div>
                  <div className="mt-0.5 font-medium tabular-nums text-accent-ink">
                    {fmtN(remaining)}
                  </div>
                </div>
              </div>
            ) : (
              <div className="mt-2 rounded bg-panel p-2 text-center text-[12px] text-text-3">
                새 재고 레코드가 생성됩니다
              </div>
            )}
          </div>
        </section>
      </div>

      <SheetFooter>
        <SheetClose asChild>
          <Button variant="outline" size="sm">
            {tCommon("close")}
          </Button>
        </SheetClose>
        {canReceive && (
          <Button size="sm" onClick={handleReceive}>
            {t("confirmReceive")}
          </Button>
        )}
      </SheetFooter>
    </>
  );
}
