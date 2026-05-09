import { getTranslations } from "next-intl/server";

interface Props {
  title: string;
  subtitle: string;
}

export async function PagePlaceholder({ title, subtitle }: Props) {
  const tCommon = await getTranslations("Common");

  return (
    <div className="px-6 py-6">
      <header className="mb-6">
        <h1 className="text-2xl font-semibold text-text">{title}</h1>
        <p className="mt-1 text-[13.5px] text-text-2">{subtitle}</p>
      </header>

      <div className="rounded-lg border border-dashed border-border bg-panel/60 p-10 text-center">
        <div className="text-[13px] font-medium text-text-2">
          {tCommon("comingSoon")}
        </div>
        <div className="mt-1 text-[12.5px] text-text-3">
          {tCommon("placeholderHint")}
        </div>
      </div>
    </div>
  );
}
