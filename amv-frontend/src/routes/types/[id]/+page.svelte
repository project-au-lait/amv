<script lang="ts">
  import { onMount } from 'svelte';
  import type { PageProps } from './$types';
  import { ScrollText, TriangleAlert } from '@lucide/svelte';

  let { data }: PageProps = $props();
  const type = data.type;
  const unsolvedCnt = type.unsolvedCnt ? type.unsolvedCnt : 0;
  const unsolvedRate = type.unsolvedRate ? (type.unsolvedRate * 100).toFixed(1) : 0;

  const openDetailsFromHash = () => {
    const hash = window.location.hash;
    if (!hash) {
      return;
    }

    const id = hash.slice(1);
    const decodedId = decodeURIComponent(id);
    const targetElement = document.getElementById(decodedId);
    if (targetElement) {
      const details = targetElement.querySelector(':scope > details') as HTMLDetailsElement | null;
      if (details) {
        details.open = true;
      }
    }
  };
</script>

<svelte:window on:hashchange={openDetailsFromHash} />

<!-- TODO: add a link to source -->
<article>
  <h2>Unsolved Status</h2>

  <label for="analysis-progress">
    Unsolved Rate: {unsolvedRate}% (Unsolved Count: {unsolvedCnt})
  </label>
  <progress id="analysis-progress" value={unsolvedRate} max="100"></progress>
</article>

<article>
  <h2>Field Summary</h2>

  <table class="list striped fixed-col-tbl">
    <thead>
      <tr>
        <th>Field Type</th>
        <th>Field Name</th>
      </tr>
    </thead>
    <tbody>
      {#each type.fields as field}
        <tr>
          <td>{field.type}</td>
          <td>{field.name}</td>
        </tr>
      {/each}
    </tbody>
  </table>
</article>

<article>
  <h2>Method Summary</h2>

  <table class="list striped fixed-col-tbl">
    <thead>
      <tr>
        <th>Return Type</th>
        <th>Method Signature</th>
        <th>Additional Info</th>
      </tr>
    </thead>
    <tbody>
      {#each type.methods as method}
        <tr>
          <td>{method.returnType}</td>
          <!-- TODO: show method.parameters -->
          <!-- TODO: add a link to call tree -->
          <td>
            <a href={`#${method.simpleSignature}`} title={method.simpleSignature}>
              {method.simpleSignature}
            </a>
            {#if method.unsolvedReason}
              <TriangleAlert class="del-color" />
            {/if}
          </td>
          <td>
            {#if method.entryPoint}
              <strong>Entry Point:</strong> {method.entryPoint.httpMethod} {method.entryPoint.path}
            {/if}
            {#if method.crudPoints.length > 0}
              {#each method.crudPoints as crudPoint, i}
                <strong style={'visibility:' + (i === 0 ? 'visible' : 'hidden')}>CRUD:</strong>
                {crudPoint.crud}: {crudPoint.dataName || crudPoint.type}<br />
              {/each}
            {/if}
          </td>
        </tr>
      {/each}
    </tbody>
  </table>
</article>

<article>
  <h2>Method Detail</h2>
  {#each type.methods as method}
    <article>
      <header id={method.simpleSignature}>
        {#if method.unsolvedReason}
          <details>
            <summary class="underline">
              <strong>{method.simpleSignature}</strong>
            </summary>
            <blockquote>{method.unsolvedReason}</blockquote>
          </details>
        {:else}
          <strong>{method.simpleSignature}</strong>
        {/if}
      </header>

      {#each method.calls as call}
        <div style="margin-left: 20px;" class="accordion-row">
          L.{call.lineNo}
          <strong>Call:</strong>
          {#if call.qualifiedSignature}
            {call.qualifiedSignature}
            <a
              href={method.srcUrl}
              title={`Go to the declaration of ${method.simpleSignature}`}
              style="margin-left:0.5rem"
            >
              <ScrollText />
            </a>
          {:else if call.interfaceSignature}
            {call.interfaceSignature} (interface)
            <a
              href={method.srcUrl}
              title={`Go to the declaration of ${method.simpleSignature}`}
              style="margin-left:0.5rem"
            >
              <ScrollText />
            </a>
          {:else}
            <details>
              <summary class="underline"
                >{call.fallbackSignature} (fallback)

                <a
                  href={method.srcUrl}
                  title={`Go to the declaration of ${method.simpleSignature}`}
                  style="margin-left:0.5rem"
                >
                  <ScrollText />
                </a>
              </summary>
              <blockquote>{call.unsolvedReason}</blockquote>
            </details>
          {/if}
        </div>
      {/each}
    </article>
  {/each}
</article>

<style>
  .accordion-row {
    display: flex;
    align-items: flex-start; /* Vertically align summary and fixed labels */
    gap: 0.5rem; /* Spacing between label and summary */
  }

  .fixed-col-tbl {
    table-layout: fixed;
    th,
    td {
      word-break: break-word;
    }
  }

  details {
    margin-bottom: 0;
    summary {
      display: flex;
      line-height: 1.5rem;
      &::after {
        height: 1.5rem;
      }
    }
  }

  .underline {
    text-decoration: underline;
  }
</style>
