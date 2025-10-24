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
  private restApi = inject(EnumService);
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
      const subscription = this.restApi
        .createEnum(this.art(), {
          code: this.form.value.code!,
          name: this.form.value.name!,
          text: this.form.value.text!,
        })
        .subscribe({
          next: (item) => {
            this.createEmitter.emit(item);
            this.visible.set(false);
            this.form.reset();
          },
        });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    } else {
      const subscription = this.restApi
        .updateEnum(this.art(), {
          code: this.form.value.code!,
          name: this.form.value.name!,
          text: this.form.value.text!,
        })
        .subscribe({
          next: (item) => {
            this.updateEmitter.emit(item);
            this.visible.set(false);
            this.form.reset();
          },
        });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
    }
  }
}
