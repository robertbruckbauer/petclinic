<script lang="ts">
  import { Tween } from "svelte/motion";
  import { linear } from "svelte/easing";
  import { toast, type ToastEntry } from "./stores.js";

  let { entry }: { entry: ToastEntry } = $props();

  // Keep top-level initialization free of prop captures; sync from entry in effects.
  const progress = new Tween(0, {
    duration: () => entry.duration,
    easing: linear,
  });
  let next = $state(0);
  let prev = $state(0);
  let paused = $state(false);
  let currentId = $state<number | null>(null);

  const onKey = (_event: KeyboardEvent) => {
    if (_event.key === "Escape") {
      toast.pop(entry.id);
    }
  };

  const onClose = () => {
    toast.pop(entry.id);
  };

  const autoclose = () => {
    if (progress.current === 1 || progress.current === 0) {
      toast.pop(entry.id);
    }
  };

  // Reset animation state when a different toast entry is rendered.
  $effect(() => {
    if (currentId !== entry.id) {
      currentId = entry.id;
      const initial = entry.initial;
      next = initial;
      prev = initial;
      paused = false;
      progress.set(initial, { duration: 0 });
    }
  });

  // Watch for changes in item.next
  $effect(() => {
    if (next !== entry.next) {
      next = entry.next;
      prev = progress.current;
      paused = false;
      progress.set(next).then(autoclose);
    }
  });

  const pause = () => {
    if (!paused && progress.current !== next) {
      progress.set(progress.current, { duration: 0 });
      paused = true;
    }
  };

  const resume = () => {
    if (paused) {
      const d = entry.duration;
      const duration = d - d * ((progress.current - prev) / (next - prev));
      progress.set(next, { duration }).then(autoclose);
      paused = false;
    }
  };
</script>

<div
  class="_toastItem bg-red-800 text-white z-30"
  role="alert"
  onmouseenter={pause}
  onmouseleave={resume}
>
  <div role="status" class="_toastMsg">
    {@html entry.message}
  </div>
  <div
    class="_toastBtn"
    role="button"
    tabindex="-1"
    onclick={onClose}
    onkeydown={onKey}
  >
    <span>✕</span>
  </div>
  <progress class="_toastBar bg-transparent" value={progress.current}
    >&nbsp;</progress
  >
</div>

<style>
  ._toastItem {
    width: 20rem;
    height: auto;
    min-height: 3.5rem;
    margin: 0 0 0.5rem 0;
    padding: 0;
    border: none;
    border-radius: 0.125rem;
    position: relative;
    display: flex;
    flex-direction: row;
    align-items: center;
    overflow: hidden;
    will-change: transform, opacity;
    pointer-events: auto;
  }
  ._toastMsg {
    padding: 0.75rem 0.5rem;
    flex: 1 1 0%;
    white-space: pre-line;
    word-break: break-all;
  }
  ._toastBtn {
    width: 2rem;
    height: 100%;
    font: 1rem sans-serif;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    outline: none;
  }
  ._toastBar {
    top: auto;
    right: auto;
    bottom: 0;
    left: 0;
    height: 6px;
    width: 100%;
    position: absolute;
    display: block;
    border: none;
    pointer-events: none;
  }
  ._toastBar::-webkit-progress-value {
    background: rgba(33, 150, 243, 0.75);
  }
  ._toastBar::-moz-progress-bar {
    background: rgba(33, 150, 243, 0.75);
  }
</style>
