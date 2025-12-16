import {
  Component,
  input,
  signal,
  OnInit,
  OnDestroy,
  computed,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { Toast, ToastEntry } from "./toast";

@Component({
  selector: "toast-item",
  standalone: true,
  imports: [CommonModule],
  styleUrl: "./toast.css",
  template: `
    <div
      class="_toastItem bg-red-800 text-white z-30"
      role="alert"
      (mouseenter)="pause()"
      (mouseleave)="resume()"
    >
      <div role="status" class="_toastMsg" [innerHTML]="message()"></div>
      <div
        class="_toastBtn"
        role="button"
        tabindex="-1"
        (click)="onClose()"
        (keydown)="onKey($event)"
      >
        <span>✕</span>
      </div>
      <progress class="_toastBar bg-transparent" [value]="progress()" max="1">
        &nbsp;
      </progress>
    </div>
  `,
})
export class ToastItemComponent implements OnInit, OnDestroy {
  constructor(private toast: Toast) {}
  private next = signal(0);
  private prev = signal(0);
  private paused = signal(false);
  private animationFrameId?: number;
  private startTime?: number;
  private pauseTime?: number;
  private remainingDuration = 0;

  entry = input.required<ToastEntry>();
  message = computed(() => this.entry().message);
  progress = signal(0);

  ngOnInit(): void {
    const entry = this.entry();
    this.prev.set(entry.initial);
    this.next.set(entry.next);
    this.progress.set(entry.initial);
    this.startTime = performance.now();
    this.remainingDuration = this.entry().duration;
    this.animate();
  }

  ngOnDestroy(): void {
    if (this.animationFrameId) {
      cancelAnimationFrame(this.animationFrameId);
    }
  }

  private animate = (): void => {
    if (this.paused()) return;

    const now = performance.now();
    const elapsed = now - (this.startTime || now);
    const duration = this.remainingDuration;

    if (duration > 0) {
      const ratio = Math.min(elapsed / duration, 1);
      const distance = this.next() - this.prev();
      this.progress.set(this.prev() + distance * ratio);
    } else {
      this.progress.set(this.next());
    }

    if (elapsed < duration) {
      this.animationFrameId = requestAnimationFrame(this.animate);
    } else {
      this.progress.set(this.next());
      this.toast.pop(this.entry().id);
    }
  };

  pause(): void {
    if (!this.paused() && this.progress() !== this.next()) {
      this.paused.set(true);
      this.pauseTime = performance.now();
      if (this.animationFrameId) {
        cancelAnimationFrame(this.animationFrameId);
      }
    }
  }

  resume(): void {
    if (this.paused()) {
      const pauseDuration = performance.now() - (this.pauseTime || 0);
      this.startTime = (this.startTime || 0) + pauseDuration;
      this.paused.set(false);
      this.animate();
    }
  }

  onKey(event: KeyboardEvent): void {
    if (event.key === "Escape") {
      this.toast.pop(this.entry().id);
    }
  }

  onClose(): void {
    this.toast.pop(this.entry().id);
  }
}
