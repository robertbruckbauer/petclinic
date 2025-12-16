import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

export interface ToastEntry {
  id: number;
  message: string;
  duration: number;
  initial: number;
  next: number;
}

const defaults = {
  duration: 60000,
  initial: 1,
  next: 0,
};

@Injectable({
  providedIn: "root",
})
export class Toast {
  private toastCount = 0;
  private allToastSubject = new BehaviorSubject<ToastEntry[]>([]);
  public allToastEntry$: Observable<ToastEntry[]> =
    this.allToastSubject.asObservable();

  push(cause: any): number {
    const entry: ToastEntry = {
      ...defaults,
      message: cause.detail || cause.toString(),
      id: ++this.toastCount,
    };
    const allEntry = this.allToastSubject.value;
    this.allToastSubject.next([entry, ...allEntry]);
    return this.toastCount;
  }

  pop(id: number): void {
    const allEntry = this.allToastSubject.value;
    if (!allEntry.length || id === 0) {
      this.allToastSubject.next([]);
      return;
    }
    this.allToastSubject.next(allEntry.filter((entry) => entry.id !== id));
  }

  set(id: number): void {
    const allEntry = [...this.allToastSubject.value];
    const entryIndex = allEntry.findIndex((entry) => entry.id === id);
    if (entryIndex > -1) {
      allEntry[entryIndex] = { ...allEntry[entryIndex], id };
    }
    this.allToastSubject.next(allEntry);
  }
}
