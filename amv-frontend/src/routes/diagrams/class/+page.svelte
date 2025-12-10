<script lang="ts">
  import type { PageProps } from './$types';
  import { goto } from '$app/navigation';
  import CriteriaUtils from '$lib/arch/search/CriteriaUtils';

  let { data }: PageProps = $props();
  // #14629:BEGIN
  let { criteria: _criteria } = $derived(data);
  // svelte-ignore state_referenced_locally
  let criteria = $state(_criteria);
  // #14629:END
  let { result } = $derived(data);
  let { classDiagram } = $derived(data);
  let canvas: HTMLDivElement;
  let scaleValue = $state(1);

  async function search() {
    await goto(CriteriaUtils.encode(criteria));
  }

  function url(qualifiedSignature: string) {
    return CriteriaUtils.encode({ text: qualifiedSignature });
  }

  $effect(() => {
    canvas.innerHTML = classDiagram;
  });
</script>

<section>
  <fieldset role="search">
    <!-- svelte-ignore a11y_autofocus -->
    <input id="search" type="search" bind:value={criteria.text} oninput={search} autofocus />
    <input type="submit" value="Search" />
  </fieldset>
</section>

<section>
  <!--#14629:BEGIN-->
  {#if criteria.text && result.list && result.list!.length > 0}
    {@const andMoreCount = result.pageResult!.count! - result.list.length}
    <ul>
      {#each result.list as type}
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
  <!--#14629:END-->
</section>

<div id="canvas" style="--val:{scaleValue}" bind:this={canvas}></div>

<style lang="scss">
  #canvas {
    text-align: center;
    transform-origin: top left;
    transform: scale(var(--val));
  }
</style>
