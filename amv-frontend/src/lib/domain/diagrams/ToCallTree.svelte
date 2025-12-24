<script lang="ts" module>
  import CriteriaUtils from '$lib/arch/search/CriteriaUtils';

  export function buildQueryString(partialSignature?: string): string {
    return CriteriaUtils.encode({ methodCriteria: { text: partialSignature } });
  }
</script>

<script lang="ts">
  import { ListTree } from '@lucide/svelte';

  interface Props {
    signaturePattern: string | undefined;
    onclick?: () => void;
  }

  let { signaturePattern, onclick }: Props = $props();
</script>

{#if signaturePattern}
  <a
    href={`/diagrams/call-tree${buildQueryString(signaturePattern)}`}
    data-tooltip={`Open Call Tree for ${signaturePattern}`}
    {onclick}
  >
    <ListTree />
  </a>
{/if}
