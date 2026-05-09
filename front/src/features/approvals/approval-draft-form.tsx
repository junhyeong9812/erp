"use client";

import { useTranslations } from "next-intl";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import {
  SheetClose,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { fmtKRW } from "@/lib/format";
import { EMPLOYEES } from "@/lib/mock/hr/employees";
import { useErpStore } from "@/store/erp-store";

const DRAFTER_ID = 1;

const draftSchema = z.object({
  documentType: z.enum(["EXPENSE", "PROCUREMENT", "LEAVE", "OTHER"]),
  title: z.string().trim().min(1),
  amount: z.coerce.number().int().min(0),
  approverIds: z.array(z.coerce.number().int().min(1)).min(1),
});

type FormValues = z.infer<typeof draftSchema>;

const DEFAULT_VALUES: FormValues = {
  documentType: "EXPENSE",
  title: "",
  amount: 0,
  approverIds: [],
};

const APPROVER_CANDIDATES = EMPLOYEES.filter(
  (e) => e.status === "ACTIVE" && e.id !== DRAFTER_ID
);

export function ApprovalDraftForm() {
  const t = useTranslations("Approvals.create");
  const tCommon = useTranslations("Common");

  const create = useErpStore((s) => s.createApprovalDraft);
  const close = useErpStore((s) => s.closeApprovalDraft);

  const {
    register,
    control,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<FormValues>({
    resolver: zodResolver(draftSchema),
    defaultValues: DEFAULT_VALUES,
  });

  const documentType = watch("documentType");
  const showAmount =
    documentType === "EXPENSE" || documentType === "PROCUREMENT";
  const amount = Number(watch("amount") || 0);

  const onSubmit = (data: FormValues) => {
    const result = create({
      documentType: data.documentType,
      title: data.title,
      amount: data.amount,
      approverIds: data.approverIds,
    });
    if (result.ok && result.value != null) {
      toast.success(t("toastCreated", { id: result.value }));
      reset(DEFAULT_VALUES);
      close();
    } else if (!result.ok) {
      toast.error(t("toastFailed"));
    }
  };

  return (
    <>
      <SheetHeader>
        <SheetTitle>{t("title")}</SheetTitle>
        <SheetDescription>{t("subtitle")}</SheetDescription>
      </SheetHeader>

      <form
        onSubmit={handleSubmit(onSubmit)}
        className="flex flex-1 flex-col overflow-hidden"
      >
        <div className="flex-1 overflow-y-auto px-5 py-4">
          {/* 유형 */}
          <div className="mb-4">
            <label
              htmlFor="documentType"
              className="mb-1 block text-[11.5px] font-medium text-text-2"
            >
              {t("typeLabel")}
            </label>
            <Select id="documentType" {...register("documentType")}>
              <option value="EXPENSE">{t("type.EXPENSE")}</option>
              <option value="PROCUREMENT">{t("type.PROCUREMENT")}</option>
              <option value="LEAVE">{t("type.LEAVE")}</option>
              <option value="OTHER">{t("type.OTHER")}</option>
            </Select>
          </div>

          {/* 제목 */}
          <div className="mb-4">
            <label
              htmlFor="title"
              className="mb-1 block text-[11.5px] font-medium text-text-2"
            >
              {t("titleLabel")}
            </label>
            <Input
              id="title"
              type="text"
              placeholder={t("titlePlaceholder")}
              aria-invalid={!!errors.title || undefined}
              className={errors.title ? "border-danger" : undefined}
              {...register("title")}
            />
            {errors.title && (
              <p className="mt-1 text-[11px] text-danger">{t("errorTitle")}</p>
            )}
          </div>

          {/* 금액 (조건부) */}
          {showAmount && (
            <div className="mb-4">
              <label
                htmlFor="amount"
                className="mb-1 block text-[11.5px] font-medium text-text-2"
              >
                {t("amountLabel")}
              </label>
              <Input
                id="amount"
                type="number"
                min={0}
                placeholder={t("amountPlaceholder")}
                aria-invalid={!!errors.amount || undefined}
                className={errors.amount ? "border-danger" : undefined}
                {...register("amount")}
              />
              {errors.amount && (
                <p className="mt-1 text-[11px] text-danger">
                  {t("errorAmount")}
                </p>
              )}
              <p className="mt-1 text-[11px] text-text-3 tabular-nums">
                {fmtKRW(amount)}
              </p>
            </div>
          )}

          {/* 결재자 multi-select */}
          <div className="mb-4">
            <div className="mb-1.5 flex items-center justify-between">
              <span className="text-[11.5px] font-medium text-text-2">
                {t("approversLabel")}
              </span>
              <span className="text-[11px] text-text-3">
                {t("approverHint")}
              </span>
            </div>
            <Controller
              control={control}
              name="approverIds"
              render={({ field }) => (
                <div className="flex flex-wrap gap-1.5">
                  {APPROVER_CANDIDATES.map((e) => {
                    const checked = field.value.includes(e.id);
                    return (
                      <button
                        type="button"
                        key={e.id}
                        onClick={() => {
                          if (checked) {
                            field.onChange(
                              field.value.filter((x) => x !== e.id)
                            );
                          } else {
                            field.onChange([...field.value, e.id]);
                          }
                        }}
                        className={
                          checked
                            ? "rounded-full border border-accent bg-accent-soft px-3 py-1 text-[12px] font-medium text-accent-ink transition-colors"
                            : "rounded-full border border-border bg-panel-2 px-3 py-1 text-[12px] text-text-2 transition-colors hover:border-text-3 hover:bg-hover"
                        }
                      >
                        {e.name} · {e.employeeNumber}
                      </button>
                    );
                  })}
                </div>
              )}
            />
            {errors.approverIds && (
              <p className="mt-1.5 text-[11px] text-danger">
                {t("errorApprovers")}
              </p>
            )}
          </div>

          {/* 단계 미리보기 */}
          <div className="mt-4 rounded-md border border-border bg-panel-2 px-4 py-3 text-[12px] text-text-2">
            {t("stepsPreview", {
              count: watch("approverIds").length,
            })}
          </div>
        </div>

        <SheetFooter>
          <SheetClose asChild>
            <Button type="button" variant="outline" size="sm">
              {tCommon("cancel")}
            </Button>
          </SheetClose>
          <Button type="submit" size="sm" disabled={isSubmitting}>
            {isSubmitting ? tCommon("loading") : t("submit")}
          </Button>
        </SheetFooter>
      </form>
    </>
  );
}
