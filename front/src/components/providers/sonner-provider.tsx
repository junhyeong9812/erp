"use client";

import { Toaster } from "sonner";
import { useTheme } from "next-themes";

export function SonnerProvider() {
  const { resolvedTheme } = useTheme();
  const theme = resolvedTheme === "dark" ? "dark" : "light";
  return (
    <Toaster
      position="top-right"
      theme={theme}
      richColors
      closeButton
      toastOptions={{
        classNames: {
          toast:
            "border border-border bg-panel text-text shadow-lg [font-family:var(--font-sans)]",
          description: "text-text-2",
        },
      }}
    />
  );
}
