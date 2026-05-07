<script lang="ts">
  import { onMount, onDestroy } from "svelte";
  import { Subject, debounceTime, distinctUntilChanged, switchMap } from "rxjs";
  import { toast } from "../../controls/Toast";
  import { InvoiceService } from "../../services/invoice.service";
  import { VisitService } from "../../services/visit.service";
  import type { VisitItem } from "../../types/visit.type";
  import type { Invoice, InvoiceStatus } from "../../types/invoice.type";

  const invoiceService = new InvoiceService();
  const visitService = new VisitService();

  interface Props {
    autofocus?: boolean;
    autoscroll?: boolean;
    visible: boolean;
    invoice: Invoice;
    oncancel?: undefined | (() => void);
    oncreate?: undefined | ((invoice: Invoice) => void);
    onupdate?: undefined | ((invoice: Invoice) => void);
  }

  let {
    autofocus = true,
    autoscroll = true,
    visible = $bindable(false),
    invoice = {} as Invoice,
    oncancel = undefined,
    oncreate = undefined,
    onupdate = undefined,
  }: Props = $props();

  let clicked = $state(false);
  let focusOn: any;
  let bottomDiv: HTMLElement;

  // Visit picker
  let visitFilter = $state("");
  let allVisitItem = $state([] as VisitItem[]);
  let selectedVisitId = $state("");

  const visitFilter$ = new Subject<string>();
  const visitSub = visitFilter$
    .pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((text) => visitService.loadAllVisitByText(text))
    )
    .subscribe({
      next: (items) => {
        allVisitItem = items;
      },
      error: (err) => {
        toast.push(err);
      },
    });

  onMount(async () => {
    if (autofocus) focusOn.focus();
    if (autoscroll) bottomDiv.scrollIntoView(false);
  });

  onDestroy(() => {
    visitSub.unsubscribe();
    visitFilter$.complete();
  });

  function onVisitFilterInput(event: Event) {
    const text = (event.target as HTMLInputElement).value;
    visitFilter = text;
    visitKeyIndex = -1;
    if (text.length >= 2) {
      visitFilter$.next(text);
    } else {
      allVisitItem = [];
    }
  }

  let visitKeyIndex = $state(-1);

  function onVisitKeyDown(event: KeyboardEvent) {
    if (!allVisitItem.length) return;
    if (event.key === "ArrowDown") {
      event.preventDefault();
      visitKeyIndex = Math.min(visitKeyIndex + 1, allVisitItem.length - 1);
    } else if (event.key === "ArrowUp") {
      event.preventDefault();
      visitKeyIndex = Math.max(visitKeyIndex - 1, 0);
    } else if (event.key === "Enter") {
      event.preventDefault();
      if (visitKeyIndex >= 0) selectVisitItem(allVisitItem[visitKeyIndex]);
    } else if (event.key === "Escape" || event.key === "Tab") {
      allVisitItem = [];
      visitKeyIndex = -1;
    }
  }

  function selectVisitItem(item: (typeof allVisitItem)[0]) {
    selectedVisitId = item.value;
    visitFilter = item.text;
    allVisitItem = [];
    visitKeyIndex = -1;
  }

  function parseDays(iso: string): number {
    const match = /PT(\d+)H/.exec(iso ?? "");
    const hours = match?.[1] ? parseInt(match[1], 10) : 0;
    return Math.round(hours / 24);
  }

  function buildPeriod(days: number): string {
    if (days === 0) return "PT0S";
    return `PT${days * 24}H`;
  }

  let newIssued = $state("");
  let newDays = $state(0);
  let newStatus = $state("D" as InvoiceStatus);
  let newText = $state("");

  $effect(() => {
    newIssued = invoice.issued ?? "";
    newDays = parseDays(invoice.period);
    newStatus = invoice.status ?? "D";
    newText = invoice.text ?? "";
    selectedVisitId = invoice.visit?.replace("/api/visit/", "") ?? "";
  });

  const newInvoice = $derived({
    ...invoice,
    issued: newIssued,
    period: buildPeriod(newDays),
    status: newStatus,
    text: newText,
    visit: selectedVisitId ? `/api/visit/${selectedVisitId}` : invoice.visit,
  });

  function onSubmitClicked(_event: Event) {
    _event.preventDefault();
    try {
      clicked = true;
      if (invoice.id) {
        invoiceService.mutateInvoice(invoice.id, newInvoice).subscribe({
          next: (json) => {
            visible = false;
            onupdate?.(json);
          },
          error: (err) => {
            toast.push(err);
          },
        });
      } else {
        invoiceService.createInvoice(newInvoice).subscribe({
          next: (json) => {
            visible = false;
            oncreate?.(json);
          },
          error: (err) => {
            toast.push(err);
          },
        });
      }
    } finally {
      clicked = false;
    }
  }

  function onCancelClicked(_event: Event) {
    _event.preventDefault();
    visible = false;
    oncancel?.();
  }
</script>

<form onsubmit={onSubmitClicked}>
  <div class="flex flex-col gap-2 pt-4">
    <div class="flex flex-col gap-2 sm:flex-row">
      <fieldset class="fieldset w-full">
        <legend class="fieldset-legend">Issue Date</legend>
        <input
          bind:this={focusOn}
          bind:value={newIssued}
          aria-label="Issue Date"
          type="date"
          class="input input-bordered w-full"
        />
      </fieldset>
      <fieldset class="fieldset w-full">
        <legend class="fieldset-legend">Payment period in days</legend>
        <input
          bind:value={newDays}
          aria-label="Payment period in days"
          type="number"
          min="0"
          class="input input-bordered w-full"
        />
      </fieldset>
      <fieldset class="fieldset w-full">
        <legend class="fieldset-legend">Status</legend>
        <select
          bind:value={newStatus}
          aria-label="Status"
          class="select w-full"
        >
          <option value="D">drafted</option>
          <option value="I">issued</option>
          <option value="C">completed</option>
          <option value="X">cancelled</option>
        </select>
      </fieldset>
    </div>
    <fieldset class="fieldset w-full">
      <legend class="fieldset-legend">Visit</legend>
      <div class="relative">
        <input
          value={visitFilter}
          oninput={onVisitFilterInput}
          onfocus={onVisitFilterInput}
          onkeydown={onVisitKeyDown}
          aria-label="Filter visits"
          aria-autocomplete="list"
          type="text"
          class="input input-bordered w-full"
          placeholder="Type to search visits…"
          autocomplete="off"
        />
        {#if allVisitItem.length > 0}
          <ul
            class="absolute z-10 w-full bg-base-100 border border-base-300 rounded-box shadow-lg mt-1 max-h-60 overflow-y-auto"
            role="listbox"
          >
            {#each allVisitItem as item, i}
              <li
                role="option"
                aria-selected={selectedVisitId === item.value}
                class="px-3 py-2 cursor-pointer"
                class:bg-primary={i === visitKeyIndex}
                class:text-primary-content={i === visitKeyIndex}
                class:hover:bg-base-200={i !== visitKeyIndex}
                onmousedown={() => selectVisitItem(item)}
              >
                {item.text}
              </li>
            {/each}
          </ul>
        {/if}
      </div>
    </fieldset>
    <fieldset class="fieldset w-full">
      <legend class="fieldset-legend">Text</legend>
      <textarea
        bind:value={newText}
        aria-label="Text"
        class="textarea w-full"
        placeholder="Enter invoice description"
      ></textarea>
    </fieldset>
  </div>
  <div class="join py-4">
    <button type="submit" class="btn join-item" disabled={clicked}>Ok</button>
    <button type="button" class="btn join-item" onclick={onCancelClicked}>
      Cancel
    </button>
  </div>
</form>

<div class="h-0" bind:this={bottomDiv}>&nbsp;</div>

{#if import.meta.env.DEV}
  <details tabindex="-1">
    <summary>JSON</summary>
    <pre>{JSON.stringify(newInvoice, null, 2)}</pre>
  </details>
{/if}
