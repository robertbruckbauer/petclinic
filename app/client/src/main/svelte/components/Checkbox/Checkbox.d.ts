import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';
export default class Checkbox extends SvelteTypedComponent<CheckboxProps, CheckboxEvents, CheckboxSlots> {
}

declare const _CheckboxProps: {

    /** 
     * Disabled state.
     */
    disabled?: boolean;

    /** 
     * Checked state.
     */
    checked?: boolean;

    /** 
     * Input label
     */
    label?: string;

    /**
     * Title for tooltip
     */
    title?: string

} & SvelteAllProps;

declare const _CheckboxEvents: {
    change: Event;
};

declare const _CheckboxSlots: {
};

export declare type CheckboxProps = typeof _CheckboxProps;
export declare type CheckboxEvents = typeof _CheckboxEvents;
export declare type CheckboxSlots = typeof _CheckboxSlots;
export {
};