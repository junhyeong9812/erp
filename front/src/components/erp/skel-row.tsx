import { Skeleton } from "@/components/ui/skeleton";

interface Props {
  cols?: number;
}

export function SkelRow({ cols = 6 }: Props) {
  return (
    <tr className="border-b border-divider">
      {Array.from({ length: cols }).map((_, i) => (
        <td key={i} className="px-4 py-3">
          <Skeleton
            className="h-3"
            style={{ width: `${40 + ((i * 17) % 50)}%` }}
          />
        </td>
      ))}
    </tr>
  );
}
