import { SvelteTypedComponent , SvelteAllProps } from 'svelte-typed-component';

export default class Toggle extends SvelteTypedComponent<ToggleProps, ToggleEvents, ToggleSlots> {
}

declare const _ToggleProps: {

    /** 
     * Disabled state.
     */
    disabled?: boolean;

    /** 
     * Input label
     */
    label?: string;

    /** 
     * Array of Items.
     */
    allItem?: ({value:string, text: string}|string)[];

    /** 
     * Selected values.
     */
    allValue?: string;

    /**
     * Title for tooltip
     */
    title?: string
    
} & SvelteAllProps;

declare const _ToggleEvents: {
    click: any;
    change: any;
};

declare const _ToggleSlots: {
};

export declare type ToggleProps = typeof _ToggleProps;
export declare type ToggleEvents = typeof _ToggleEvents;
export declare type ToggleSlots = typeof _ToggleSlots;
export {};
