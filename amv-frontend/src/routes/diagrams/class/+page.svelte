<script lang="ts">
  import type { PageProps } from './$types';
  import { goto } from '$app/navigation';
  import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
  import Diagram from '$lib/domain/diagrams/Diagram.svelte';
  import type { CriteriaModel } from './+page';

  let { data }: PageProps = $props();
  let { criteria: _criteria, typeResult, classDiagram } = $derived(data);
  // svelte-ignore state_referenced_locally
  let criteria = $state(_criteria);

  $effect(() => {
    criteria = _criteria;
  });

  $effect(() => {
    search(criteria);
  });

  async function search(criteria: CriteriaModel) {
    await goto(CriteriaUtils.encode(criteria));
  }

  function url(qualifiedSignature: string | undefined) {
    return CriteriaUtils.encode({ typeSearchCriteriaModel: { text: qualifiedSignature } });
  }
</script>

<section class="container">
  <fieldset role="search">
    <!-- svelte-ignore a11y_autofocus -->
    <input id="search" type="search" bind:value={criteria.typeSearchCriteriaModel.text} autofocus />
  </fieldset>
</section>

<section>
  {#if criteria.typeSearchCriteriaModel.text && typeResult.list && typeResult.list!.length > 0}
    {@const andMoreCount = typeResult.pageResult!.count! - typeResult.list.length}
    <ul>
      {#each typeResult.list as type}
        <div>
          <a href={url(type.qualifiedName!)}>{type.qualifiedName}</a>
        </div>
      {/each}
      {#if andMoreCount > 0}
        <div>
          ...and {andMoreCount} more
        </div>
      {/if}
    </ul>
  {/if}
</section>

{#if classDiagram?.image}
  <section class="container">
    <Diagram diagram={classDiagram} />
  </section>
{/if}
