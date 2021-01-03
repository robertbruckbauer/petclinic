<script>
  import { onDestroy } from "svelte";
  import { tweened } from "svelte/motion";
  import { linear } from "svelte/easing";
  import { toast } from "./stores.js";
  export let item;
  const progress = tweened(item.initial, {
    duration: item.duration,
    easing: linear,
  });
  const close = () => toast.pop(item.id);
  const autoclose = () => {
    if ($progress === 1 || $progress === 0) {
      close();
    }
  };
  let next = item.initial;
  let prev = next;
  let paused = false;
  $: if (next !== item.next) {
    next = item.next;
    prev = $progress;
    paused = false;
    progress.set(next).then(autoclose);
  }
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
  const getProps = () => {
    const { props = {}, sendIdTo } = item.component;
    if (sendIdTo) {
      props[sendIdTo] = item.id;
    }
    return props;
  };
  onDestroy(() => {
    if (typeof item.onpop === "function") {
      item.onpop(item.id);
    }
  });
</script>

<div
  class="_toastItem bg-error-800 text-white z-30"
  on:mouseenter={pause}
  on:mouseleave={resume}
>
  <div role="status" class="_toastMsg">
    {#if item.component}
      <svelte:component this={item.component.src} {...getProps()} />
    {:else}
      {@html item.msg}
    {/if}
  </div>
  {#if item.dismissable}
    <div class="_toastBtn" role="button" tabindex="-1" on:click={close}>
      ✕
    </div>
  {/if}
  <progress class="_toastBar bg-transparent" value={$progress} />
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
    background: rgba(33, 150, 243, 0.75)
  }
  ._toastBar::-moz-progress-bar {
    background: rgba(33, 150, 243, 0.75)
  }
</style>
