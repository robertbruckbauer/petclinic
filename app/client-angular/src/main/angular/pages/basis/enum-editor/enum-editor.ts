import {
  Component,
  DestroyRef,
  OnInit,
  inject,
  input,
  model,
  output,
} from "@angular/core";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { Toast } from "../../../controls/toast/toast";
import { EnumService } from "../../../services/enum.service";
import { type EnumItem } from "../../../types/enum.type";

@Component({
  selector: "app-enum-editor",
  imports: [ReactiveFormsModule],
  templateUrl: "./enum-editor.html",
  styles: ``,
})
export class EnumEditorComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private toast = inject(Toast);
  private enumService = inject(EnumService);
  art = input.required<string>();
  mode = input.required<"create" | "update">();
  visible = model.required<boolean>();
  item = input.required<EnumItem>();
  form = new FormGroup({
    code: new FormControl(0, Validators.required),
    name: new FormControl("", Validators.required),
    text: new FormControl("", Validators.required),
  });

  ngOnInit() {
    this.form.patchValue(this.item());
  }

  get isSubmittable() {
    return this.form.dirty && this.form.valid;
  }

  cancelEmitter = output<EnumItem>({ alias: "cancel" });
  onCancelClicked() {
    this.cancelEmitter.emit(this.item());
    this.visible.set(false);
    this.form.reset();
  }

  createEmitter = output<EnumItem>({ alias: "create" });
  updateEmitter = output<EnumItem>({ alias: "update" });
  onSubmitClicked() {
    if (this.mode() === "create") {
      const subscription = this.enumService
        .createEnum(this.art(), {
          ...this.item(),
          name: this.form.value.name!,
          text: this.form.value.text!,
        })
        .subscribe({
          next: (item) => {
            this.createEmitter.emit(item);
            this.visible.set(false);
            this.form.reset();
          },
          error: (err) => {
            this.toast.push(err);
          },
        });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    } else {
      const subscription = this.enumService
        .updateEnum(this.art(), {
          ...this.item(),
          name: this.form.value.name!,
          text: this.form.value.text!,
        })
        .subscribe({
          next: (item) => {
            this.updateEmitter.emit(item);
            this.visible.set(false);
            this.form.reset();
          },
          error: (err) => {
            this.toast.push(err);
          },
        });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    }
  }
}
