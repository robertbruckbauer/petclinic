import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Toast } from "./toast";
import { ToastItemComponent } from "./toast-item";

@Component({
  selector: "toast-list",
  standalone: true,
  imports: [CommonModule, ToastItemComponent],
  styleUrl: "./toast.css",
  template: `
    <ul class="_toastContainer">
      @for (entry of toast.allToastEntry$ | async; track entry.id) {
        <li class="_toastListItem">
          <toast-item [entry]="entry" />
        </li>
      }
    </ul>
  `,
})
export class ToastListComponent {
  constructor(public toast: Toast) {}
}
