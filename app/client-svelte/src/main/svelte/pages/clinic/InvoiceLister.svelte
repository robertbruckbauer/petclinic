<script lang="ts">
  import { onMount } from "svelte";
  import { toast } from "../../controls/Toast";
  import { InvoiceService } from "../../services/invoice.service";
  import type { Invoice } from "../../types/invoice.type";
  import InvoiceEditor from "./InvoiceEditor.svelte";

  const invoiceService = new InvoiceService();

  let loading = $state(true);
  onMount(async () => {
    try {
      loading = true;
      loadAllInvoice();
    } finally {
      loading = false;
    }
  });

  let invoiceId = $state("");
  function onInvoiceClicked(_invoice: Invoice) {
    invoiceId = _invoice.id!;
  }
  function onInvoiceRemoveClicked(_invoice: Invoice) {
    invoiceId = _invoice.id!;
    removeInvoice(_invoice);
  }

  let invoiceEditorCreate = $state(false);
  function onInvoiceEditorCreateClicked() {
    invoiceId = "";
    invoiceEditorCreate = true;
    invoiceEditorUpdate = false;
  }

  let invoiceEditorUpdate = $state(false);
  function onInvoiceEditorUpdateClicked(_invoice: Invoice) {
    invoiceId = _invoice.id!;
    invoiceEditorUpdate = true;
    invoiceEditorCreate = false;
  }

  const invoiceFilterDisabled = $derived(
    invoiceEditorCreate || invoiceEditorUpdate
  );

  const invoiceEditorDisabled = $derived(invoiceFilterDisabled);

  let allInvoice: Invoice[] = $state([]);
  function afterCreateInvoice(_invoice: Invoice) {
    allInvoice = [_invoice, ...allInvoice];
  }
  function afterUpdateInvoice(_invoice: Invoice) {
    allInvoice = allInvoice.map((e) => (e.id === _invoice.id ? _invoice : e));
  }
  function afterRemoveInvoice(_invoice: Invoice) {
    allInvoice = allInvoice.filter((e) => e.id !== _invoice.id);
  }

  let invoiceFilter = $state("");
  function onFilterClicked(_event: Event) {
    _event.preventDefault();
    try {
      loading = true;
      loadAllInvoice();
    } finally {
      loading = false;
    }
  }

  function loadAllInvoice() {
    const search = {
      sort: "issued,desc",
      ...(invoiceFilter ? { text: "%" + invoiceFilter + "%" } : {}),
    };
    invoiceService.loadAllInvoice(search).subscribe({
      next: (json) => {
        allInvoice = json;
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }

  function removeInvoice(_invoice: Invoice) {
    const hint = _invoice.issued;
    if (!confirm("Delete invoice '" + hint + "' permanently?")) return;
    invoiceService.removeInvoice(_invoice.id!).subscribe({
      next: (json) => {
        afterRemoveInvoice(json);
      },
      error: (err) => {
        toast.push(err);
      },
    });
  }

  const today = new Date().toISOString().substring(0, 10);
  const newInvoice = $derived<Invoice>({
    version: 0,
    issued: today,
    period: "PT0S",
    status: "D",
    text: "",
  });
</script>

<h1>Invoice</h1>

<div class="flex flex-col gap-1 ml-2 mr-2">
  <form onsubmit={onFilterClicked}>
    <div class="flex flex-row gap-2 items-center pb-2 pr-2">
      <input
        bind:value={invoiceFilter}
        aria-label="Filter"
        type="text"
        class="input input-bordered w-full"
        readonly={invoiceFilterDisabled}
        placeholder="Enter filter criteria"
      />
      <button
        type="submit"
        title="Filter items"
        class="btn btn-circle btn-outline"
        disabled={invoiceFilterDisabled}
      >
        <span class="material-icons">search</span>
      </button>
    </div>
  </form>
  {#if loading}
    <div class="h-screen flex justify-center items-start">
      <span class="loading loading-spinner loading-xl"></span>
    </div>
  {:else}
    <table class="table-fixed">
      <thead class="justify-between">
        <tr class="bg-gray-200">
          <th class="px-2 py-3 text-left w-1/4 table-cell">
            <span class="text-gray-600">Issue Date</span>
          </th>
          <th class="px-2 py-3 text-left w-full table-cell">
            <span class="text-gray-600">Text</span>
          </th>
          <th class="px-2 py-3 text-right w-16 table-cell">
            <button
              title="Add a new invoice"
              class="btn btn-circle btn-outline"
              onclick={onInvoiceEditorCreateClicked}
              disabled={invoiceEditorDisabled}
            >
              <span class="material-icons">add</span>
            </button>
          </th>
        </tr>
      </thead>
      <tbody>
        {#if invoiceEditorCreate}
          <tr>
            <td class="border-l-4 px-2" colspan="3">
              <InvoiceEditor
                oncreate={afterCreateInvoice}
                bind:visible={invoiceEditorCreate}
                invoice={newInvoice}
              />
            </td>
          </tr>
        {/if}
        {#each allInvoice as invoice}
          <tr
            onclick={() => onInvoiceClicked(invoice)}
            title={invoice.issued}
            class:border-l-2={invoiceId === invoice.id}
          >
            <td class="px-2 py-3 text-left table-cell">
              {invoice.issued}
            </td>
            <td class="px-2 py-3 text-left table-cell">
              {invoice.text}
            </td>
            <td class="px-2 py-3 table-cell">
              <div
                class="grid grid-cols-1 md:grid-cols-2 items-center gap-1 w-max"
              >
                <button
                  title="Delete an invoice"
                  class="btn btn-circle btn-outline"
                  onclick={() => onInvoiceRemoveClicked(invoice)}
                  disabled={invoiceEditorDisabled}
                >
                  <span class="material-icons">delete</span>
                </button>
                <button
                  title="Edit an invoice"
                  class="btn btn-circle btn-outline"
                  onclick={() => onInvoiceEditorUpdateClicked(invoice)}
                  disabled={invoiceEditorDisabled}
                >
                  <span class="material-icons">edit</span>
                </button>
              </div>
            </td>
          </tr>
          {#if invoiceEditorUpdate && invoiceId === invoice.id}
            <tr>
              <td class="border-l-4 px-2" colspan="3">
                <InvoiceEditor
                  onupdate={afterUpdateInvoice}
                  bind:visible={invoiceEditorUpdate}
                  {invoice}
                />
              </td>
            </tr>
          {/if}
        {:else}
          <tr>
            <td class="px-2" colspan="3">No invoices</td>
          </tr>
        {/each}
      </tbody>
    </table>
  {/if}
</div>
