<script lang="ts">
  import { tweened } from "svelte/motion";
  import { linear } from "svelte/easing";
  import { toast } from "./stores.js";

  let { item } = $props();

  const progress = tweened(item.initial, {
    duration: item.duration,
    easing: linear,
  });

  let next = $state(item.initial);
  let prev = $state(item.initial);
  let paused = $state(false);

  const onKey = (_event: KeyboardEvent) => {
    if (_event.key === "Escape") {
      toast.pop(item.id);
    }
  };

  const onClose = () => {
    toast.pop(item.id);
  };

  const autoclose = () => {
    if ($progress === 1 || $progress === 0) {
      toast.pop(item.id);
    }
  };

  // Watch for changes in item.next
  $effect(() => {
    if (next !== item.next) {
      next = item.next;
      prev = $progress;
      paused = false;
      progress.set(next).then(autoclose);
    }
  });

  const pause = () => {
    if (!paused && $progress !== next) {
      progress.set($progress, { duration: 0 });
      paused = true;
    }
  };

  const resume = () => {
    if (paused) {
      const d = item.duration;
      const duration = d - d * (($progress - prev) / (next - prev));
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
    {@html item.msg}
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
  <progress class="_toastBar bg-transparent" value={$progress}>&nbsp;</progress>
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
